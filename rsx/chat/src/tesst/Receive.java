package tesst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receive {

	public static void main(String[] args) throws IOException {
	MulticastSocket s=new MulticastSocket(7654);
	InetAddress group=InetAddress.getByName("224.0.0.1");
	s.joinGroup(group);
	for(int i=0;i<10;i++)
	{
		byte[] buffer=new byte[256];
		DatagramPacket dp=new DatagramPacket(buffer, buffer.length);
		s.receive(dp);
		System.out.println(new String(dp.getData()));
		s.leaveGroup(group);
		s.close();
		
		
	}

	}

}
