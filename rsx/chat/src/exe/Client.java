package exe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class Client {
    private void start() {
        try {
        	String message="java ReceiveUdp 1500,bonjour";
        	DatagramSocket client = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost");
            int port = 8088;
            System.out.println("clent you say");
            byte[] sendData=message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, address, port);
            client.send(sendPacket);
            
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            client.receive(recvPacket);
            String recvStr = new String(recvPacket.getData(), 0,
                    recvPacket.getLength());
            System.out.println("serveur dit:" + recvStr);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}