package Mult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
public class MultClient {
    private void start() {
        try {
        	InputStreamReader isr=new InputStreamReader(System.in);
          	BufferedReader br=new BufferedReader(isr);
            MulticastSocket client = new MulticastSocket();
            InetAddress address = InetAddress.getByName("224.0.0.1");
            int port = 7654;
            while(true)
            {System.out.println("clent you say");
            byte[] sendData=new byte[1024];
            String message=br.readLine();
            sendData=message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, address, port);
            client.send(sendPacket);
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            client.receive(recvPacket);
            String recvStr = new String(recvPacket.getData(), 0,
                    recvPacket.getLength());
            System.out.println("serveur dit:" + recvStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	MultClient client = new MultClient();
        client.start();
    }
}
