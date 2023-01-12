package entity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

// 消息类
public class Message {
    // 转发对象
    private String acceptName;
    // 转发消息
    private String message;
    // 发送者
    private String sendName;
    // 发送时间
    private Long sendTime;

    // 构造方法一，所有
    public Message(String acceptName, String message, String sendName, Long sendTime) {
        this.acceptName = acceptName;
        this.message = message;
        this.sendName = sendName;
        this.sendTime = sendTime;
    }
    // 构造方法二，通过字符串解析创建对象
    public Message(String messageString) {
        try {
            String[] strings = messageString.trim().split(",");
            for (String s : strings) {
                String[] property = s.trim().split(":");
                String name = property[0].trim();
                String value = property[1].trim();
                switch (name) {
                    case "acceptName":
                        acceptName = value;
                        break;
                    case "message":
                        message = value;
                        break;
                    case "sendName":
                        sendName = value;
                        break;
                    case "sendTime":
                        sendTime = Long.parseLong(value);
                        break;
                }
            }
        } catch (Exception ignored) { }
    }

    /**
     * get
     * set
     * toString
     * equals
     */
    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "acceptName:" + acceptName +
                ",message:" + message +
                ",sendName:" + sendName +
                ",sendTime:" + sendTime +
                "\n";
    }
}
