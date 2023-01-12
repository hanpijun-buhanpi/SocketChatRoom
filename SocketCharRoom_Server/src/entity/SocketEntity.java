package entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// 套接字实例类
public class SocketEntity {
    // 套接字
    private Socket socket;
    // 套接字名字/ip地址
    private String name;
    // 活跃时间
    private Long activeTime;
    // 数据是否已接收（因为目前的执行流程导致会重复执行多次读取操作，故此添加这个来判断）
    private boolean isRead = false;

    // 构造方法一：接收socket，默认name=ip地址
    public SocketEntity(Socket socket) {
        this.socket = socket;
        name = socket.getInetAddress().getHostAddress();
        activeTime = System.currentTimeMillis();
    }
    // 构造方法二：接收socket和name
    public SocketEntity(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        activeTime = System.currentTimeMillis();
    }

    // 判断是否有数据输入
    public int available() throws IOException {
        return socket.getInputStream().available();
    }

    /**
     * get
     * set
     * toString
     * equals
     */
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Long activeTime) {
        this.activeTime = activeTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
