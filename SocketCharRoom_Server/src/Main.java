import config.Configuration;
import controller.MySocketServer;
import service.TaskPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        MySocketServer mySocketServer = MySocketServer.getInstance(8080);
        TaskPool taskPool = TaskPool.getInstance();
    }
}