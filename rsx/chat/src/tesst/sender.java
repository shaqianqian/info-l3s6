package tesst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class sender {
public static void main(String[] args) throws IOException {
	System.out.println("server starting");
	MulticastSocket s=new MulticastSocket();
	InetAddress group=InetAddress.getByName("224.0.0.1");
	byte[] dummy=new byte[0];
	DatagramPacket dp=new DatagramPacket(dummy,0,group,7654);
	for(int i=0;i<10;i++)
	{
		byte[] buffer=("line"+i).getBytes();
		 dp.setData(buffer);
		 dp.setLength(buffer.length);
		s.send(dp);
	}
		s.close();
		
		
	}
	
	
	
}

