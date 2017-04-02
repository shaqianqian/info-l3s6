package Mult;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class MultServer {
    public void start() {
        try {

        	MulticastSocket server = new MulticastSocket(7654);
        	InetAddress group =InetAddress.getByName("224.0.0.1"); 
        	server.joinGroup(group);
        	while(true){
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData,
            		recvData.length);
            server.receive(recvPacket);
            String recvStr = new String(recvPacket.getData(), 0,
                    recvPacket.getLength());
        
            System.out.println("client says:" + recvStr);
         }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
}
    public static void main(String[] args) {
        MultServer server = new MultServer();
        server.start();
    }}