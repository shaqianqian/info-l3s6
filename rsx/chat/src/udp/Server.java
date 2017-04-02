package udp;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class Server {
    public void start() {
        try {

            DatagramSocket server = new DatagramSocket(8088);
            while(true){
            byte[] recvData = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(recvData,
            		recvData.length);
            server.receive(recvPacket);
            String recvStr = new String(recvPacket.getData(), 0,
                    recvPacket.getLength());
            int port = recvPacket.getPort();
            InetAddress address = recvPacket.getAddress();
            System.out.println("client "+address+"says:" + recvStr);
          
            String message=recvStr.toUpperCase();
            byte[] sendData=new byte[1024];
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, address, port);
            server.send(sendPacket);}
           
        } catch (Exception e) {
            e.printStackTrace();
        }
}
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

