package config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    // 实例
    private static Configuration instance;

    // 配置属性
    private static int socketPort;  // 套接字端口
    private static int threadPoolSize;  // 线程池大小
    private static int threadWaitTime;  // 线程空闲等待时间
    private static int connectedLiveTime;   // 连接存活时间

    // 创建和获取实例
    public static void createInstance(String filePath) {
        if (instance == null) {
            instance = new Configuration(filePath);
        }
    }
    public static Configuration getInstance() {
        return instance;
    }

    // 封闭构造方法
    private Configuration(String filePath) {
        File file = new File(filePath);
        int count = 0;
        // 配置文件存在则使用文件初始化，否则使用默认初始化，并创建配置文件
        if (file.exists()) {
            // 通过文件初始化，出错重试5词，再不行，打印错误并使用默认初始化
            while (true) {
                try {
                    fileInit(file);
                    break;
                } catch (Exception e) {
                    count++;
                    if (count >= 5) {
                        e.printStackTrace();
                        defaultInit();
                        break;
                    }
                }
            }
        } else {
            defaultInit();
            // 创建配置文件，出错重试5次，再不行，打印错误并退出
            while (true) {
                try {
                    createConfig(file);
                    break;
                } catch (Exception e) {
                    count++;
                    if (count >= 5) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
    }

    // 默认初始化
    private static void defaultInit() {
        socketPort = 8080;
        threadPoolSize = 5;
        connectedLiveTime = 30000;
        threadWaitTime = 10;
    }

    // 文件初始化
    private static void fileInit(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Map<String, String> map = new HashMap<>();
        String data;
        while ((data = bufferedReader.readLine()) != null) {
            if (data.contains("=")) {
                String[] temp = data.split("=");
                map.put(temp[0], temp[1]);
            }
        }
        for (String s : map.keySet()) {
            try {
                Configuration.class.getDeclaredMethod("set" + s, String.class).invoke(Configuration.class, map.get(s));
            } catch (Exception ignored) { }
        }
    }

    // 配置文件创建
    private static void createConfig(File file) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(configurationData());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    // 返回配置信息
    private static String configurationData() {
        return "# Configuration" +
                "\nSocketPort=" + socketPort +
                "\nThreadPoolSize=" + threadPoolSize +
                "\nConnectedLiveTime=" + connectedLiveTime +
                "\nThreadWaitTime" + threadWaitTime +
                "\n";
    }

    public int getSocketPort() {
        return socketPort;
    }

    private static void setSocketPort(String s) {
        socketPort = Integer.parseInt(s);
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    private static void setThreadPoolSize(String s) {
        threadPoolSize = Integer.parseInt(s);
    }

    public int getConnectedLiveTime() {
        return connectedLiveTime;
    }

    private static void setConnectedLiveTime(String s) {
        connectedLiveTime = Integer.parseInt(s);
    }

    public int getThreadWaitTime() {
        return threadWaitTime;
    }

    private static void setThreadWaitTime(String s) {
        threadWaitTime = Integer.parseInt(s);
    }
}
