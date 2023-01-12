import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// 暂用，未优化！未优化！未优化！
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static String name = "127.0.0.1";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E  HH:mm:ss");
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1",8080);   // 服务器地址和端口
        name = socket.getInetAddress().getHostAddress();
        br(socket.getInputStream());
        bw(socket.getOutputStream());
    }

    private static void br(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Thread thread = new Thread(() -> {
            while (true) {
                String temp = "";
                StringBuffer sb = new StringBuffer();
                try {
                    temp = bufferedReader.readLine();
                } catch (Exception ignored) { continue; }
                String[] message = temp.split(",");
                for (String s : message) {
                    String[] strings = s.split(":");
                    if (strings[0].equals("sendName")) {
                        sb.append("\t发送人: ").append(strings[1]);
                    } else if (strings[0].equals("sendTime")) {
                        sb.append("\t发送时间: ").append(dateFormat.format(Long.parseLong(strings[1])));
                    } else if (strings[0].equals("message")) {
                        sb.append("消息: ").append(strings[1]);
                    }
                }
                System.out.println(sb.toString());
            }
        });
        thread.setName("br");
        thread.start();
    }

    private static void bw(OutputStream outputStream) {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        Thread thread = new Thread(() -> {
            while (true) {
                String temp = "";
                if (scanner.hasNext()) {
                    temp = scanner.next();
                }
                // acceptName接收IP temp消息 sendName发送ip sendTime发送时间
                temp = "acceptName:server,message:" + temp + ",sendName:" + name + ",sendTime:" + System.currentTimeMillis() + "\n";
                try { bufferedWriter.write(temp); bufferedWriter.flush(); } catch (Exception ignored) { continue; }
            }
        });
        thread.setName("bw");
        thread.start();
    }
}