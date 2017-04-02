package chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * 服务端应用程序
*/
public class Server {
    // 服务端Socket
    private ServerSocket serverSocket;
    /**
     * 构造方法，用于初始化
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(8088);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 服务端开启方法
     */
    public void start() {
        try {
            //循环监听客户端的连接
            while(true){
                System.out.println("等待客户端连接...");
                // 监听客户端的连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接!");
                
                //启动一个线程来完成针对该客户端的交互
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    /**
     * 线程体，用于并发处理不同客户端的交互
     */
    private class ClientHandler implements Runnable {
        // 该线程用于处理的客户端
        private Socket socket;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                InputStream in  = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                // 循环读取客户端发送的信息
                while (true) {
                    System.out.println("客户端说:" + br.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}