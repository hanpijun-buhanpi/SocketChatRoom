package service;

import controller.MySocketServer;
import entity.Message;

// 消息解析类
public class ParseMessage {
    /**
     * 主调用方法
     * @param message 消息类
     * @return 解析后需要执行的任务
     */
    public static Task parseMessage(Message message) {
        Task task = null;
        if (message.getAcceptName().equals("server")) {
            task = requestServiceMessage(message);
        } else {
            task = plainMessage(message);
        }
        return task;
    }

    // 普通消息，直接加进消息列表
    private static Task plainMessage(Message message) {
        return () -> {
            MySocketServer.getInstance().addMessage(message);
        };
    }

    // 服务请求消息
    private static Task requestServiceMessage(Message message) {
        return () -> {
            if (message.getMessage().equals("onlineUser")) {
                MySocketServer.getInstance().paintUser();
            }
        };
    }
}
