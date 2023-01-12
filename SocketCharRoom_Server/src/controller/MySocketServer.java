package controller;

import entity.Message;
import entity.SocketEntity;
import service.ParseMessage;
import service.Task;
import service.TaskPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 套接字服务器类
public class MySocketServer {
    // 本类使用单例模式
    private static MySocketServer socketServer;

    // 套接字服务器
    private ServerSocket serverSocket;
    // 套接字实例列表
    private Map<String, SocketEntity> socketEntityMap;
    // 待转发列表
    private List<Message> messageList;


    // 私有化构造一，初始化变量
    private MySocketServer() {
        socketEntityMap = new HashMap<>();
        messageList = new ArrayList<>();
    }
    // 私有化构造二，初始化变量并创建服务器和运行读取和输出线程
    private MySocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        socketEntityMap = new HashMap<>();
        messageList = new ArrayList<>();
        accept();
        read();
        write();
    }

    // 获取单例一，对应构造一
    public static MySocketServer getInstance() {
        if (socketServer == null) {
            socketServer = new MySocketServer();
        }
        return socketServer;
    }
    // 获取单例二，对应单例二
    public static MySocketServer getInstance(int port) throws IOException {
        if (socketServer == null) {
            socketServer = new MySocketServer(port);
        }
        return socketServer;
    }


    // 创建服务器
    public void createSever(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    // 监听连接
    public void accept() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    // 分析连接存入列表（设置keepAlive为true，开启默认的系统的超时空闲连接判断），并唤醒等待的读取线程
                    socket.setKeepAlive(true);
                    SocketEntity socketEntity = new SocketEntity(socket);
                    socketEntityMap.put(socketEntity.getName(), socketEntity);
                    System.out.println(socketEntity.getName() + "已连接");
                } catch (IOException ignored) { }
            }
        });
        thread.setName("Thread-accept");
        thread.start();
    }
    // 遍历读取
    public void read() {
        Thread thread = new Thread(() -> {
            while (true) {
                for (String name : socketEntityMap.keySet()) {
                    SocketEntity socketEntity = socketEntityMap.get(name);
                    try {
                        // 判断是否有数据输入
                        if (socketEntity.getSocket().getInputStream().available() > 0 && !socketEntity.isRead()) {
                            socketEntity.setRead(true);
                            readTask(socketEntity);
                            // 更新活跃时间
                            socketEntity.setActiveTime(System.currentTimeMillis());
                        }
                    } catch (Exception e) {
                        // 当出错移除连接
                        socketEntityMap.remove(socketEntity.getName());
                        closeSocket(socketEntity);
                    }
                }
                // 遍历一遍暂停一下
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) { }
            }
        });
        thread.setName("Thread-read");
        thread.start();
    }
    // 读取操作
    private void readTask(SocketEntity socket) {
        Message message = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getSocket().getInputStream()));
            message = new Message(bufferedReader.readLine());
            socket.setRead(false);
        } catch (Exception ignored) { }
        Task task = ParseMessage.parseMessage(message);
        TaskPool.getInstance().registry(task);
    }
    // 遍历输出
    public void write() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    for (Message message : messageList) {
                        // 当接收者存在时，执行输出操作
                        if (socketEntityMap.containsKey(message.getAcceptName())) {
                            writeTask(message);
                            messageList.remove(message);
                        }
                    }
                    // 遍历一遍，暂停一下
                    Thread.sleep(1);
                } catch (Exception ignored) { }
            }
        });
        thread.setName("Thread-write");
        thread.start();
    }
    // 输出操作
    private void writeTask(Message message) {
        Task task = () -> {
            SocketEntity socketEntity = null;
            try {
                socketEntity = socketEntityMap.get(message.getAcceptName());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketEntity.getSocket().getOutputStream()));
                bufferedWriter.write(message.toString());
                bufferedWriter.flush();
            } catch (Exception e) {
                // 出错移除连接，并将message放回列表
                socketEntityMap.remove(socketEntity.getName());
                closeSocket(socketEntity);
                messageList.add(message);
            }
        };
        TaskPool.getInstance().registry(task);
    }

    // 连接关闭与垃圾回收（将对象设为0，让java的垃圾回收机制强制删除关闭连接）
    private static void closeSocket(SocketEntity socketEntity) {
        System.out.println(socketEntity.getName() + "已断开连接");
        try {
            socketEntity.getSocket().close();
        } catch (Exception ignored) {
        } finally {
            socketEntity = null;
        }
    }

    // 其余方法

    // 消息添加
    public void addMessage(Message message) {
        messageList.add(message);
    }

    // 打印已连接的用户
    public void paintUser() {
        System.out.println("当前的在线用户有：");
        for (String name : socketEntityMap.keySet()) {
            SocketEntity socketEntity = socketEntityMap.get(name);
            System.out.println(socketEntity.getName());
        }
    }

    // 遍历套接字，并关闭错误的连接（暂用，无用，考虑更换）
    public void socketLive() {
        for (String name : socketEntityMap.keySet()) {
            SocketEntity socketEntity = socketEntityMap.get(name);
            if (socketEntity.getSocket().isClosed() || System.currentTimeMillis() - socketEntity.getActiveTime() > 5 * 60 * 1000) {
                closeSocket(socketEntity);
                socketEntityMap.remove(socketEntity);
                System.out.println(socketEntity.getName() + "已断开连接");
                continue;
            }
            try {
                socketEntity.getSocket().getInputStream();
            } catch (IOException e) {
                closeSocket(socketEntity);
                socketEntityMap.remove(socketEntity);
                System.out.println(socketEntity.getName() + "已断开连接");
            }
        }
    }
}
