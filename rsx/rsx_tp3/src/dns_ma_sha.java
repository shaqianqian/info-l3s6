
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.lang.String;


public class dns_ma_sha {
	 static String label = "www.lifl.fr"; 
     static int reponse;


    public static void main (String[] args) throws Exception {

   //test 
	InetAddress dst = InetAddress.getByName("8.8.8.8");
   //InetAddress dst = InetAddress.getByName("172.18.12.9");

	    DatagramSocket ds;
		DatagramPacket dp;
		DatagramPacket dpr = new DatagramPacket(new byte[512], 512);
		int port = 53;
		byte[] msg =new byte[18 + label.length()] ;
        String[] labelSplit = label.split("[.]"); // "[.]" = regexp pour le point "."
           int k=0;
           msg[0] = (byte) 0x08;
           msg[1] = (byte) 0xbb;
           msg[3] = msg[4] = msg[27] = msg[25] = msg[24]=(byte) 0;
           msg[2] = msg[5] = msg[28] =msg[26] = (byte) 0x01;
       	for(k = 6; k < 12; k++)
       		msg[k] = (byte) 0;

       	for(String str : labelSplit) {
       		msg[k++] = (byte) (str.length() & 0xff); 
       	    for(char c : str.toCharArray())
       	    	msg[k++] = (byte) c; }
		
		dp= new DatagramPacket(msg, msg.length, dst, port);
		ds = new DatagramSocket();
		ds.send(dp);
		ds.receive(dpr);
		byte[] msgR = dpr.getData();

		for(int i = 0; i < dpr.getLength(); i++) {
		    System.out.print("," + HextoStr(msgR[i]));
		    if(i % 16 == 0)
			System.out.println("");
		}
		System.out.println("");

		int i, ip;

		i =31; 
		// on va jusqu'au premier champ
		while((msgR[i] & 0xff)*256 + (msgR[i+1] & 0xff) != 1)
		i += (msgR[i+8] & 0xff)*256 + (msgR[i+9] & 0xff) + 12; 
        ip = (msgR[i+10] & 0xff)*16777216 + (msgR[i+11] & 0xff)*65536 +
				(msgR[i+12] & 0xff)*256 + (msgR[i+13] & 0xff);
		question4(dpr);
	    ds.close();

    }


    //Q5

     
    public static void question4(DatagramPacket packet) {
	byte[] msg = packet.getData();
	int length = packet.getLength();
	int i = 0;
	int j, offset, nbchar, autorite, additionel, type;
	String ip = null;
   String enete=HextoStr(msg[i++]) + ',' + HextoStr(msg[i++]);
	System.out.println("ENTETE: " + enete);

	int para=(msg[i] & 0xff)*256 + (msg[i+1] & 0xff); 
	int k;
	int max=16;	
	String s = "";
	int r = para;
	s=Integer.toBinaryString(r);
	char[] champ = s.toCharArray();
	System.out.println("    QR : " + champ[0]);
	System.out.print("    OPCODE : ");
	for(k = 1; k < 5; k++)
	    System.out.print(champ[k]);
	System.out.println("\n    AA : " + champ[5]);
	System.out.println("    TC :" + champ[6]);
	System.out.println("    RD : " + champ[7]);
	System.out.println("    RA : " + champ[8]);
	System.out.println("    UNUSED : " + champ[9]);
	System.out.println("    AD :" + champ[10]);
	System.out.println("    CD :" + champ[11]);
	System.out.print("    RCODE :");
	for(j = 12; j < 16; j++)
	    System.out.print(champ[j]);
	System.out.println("");
    
	i += 2;

	System.out.println("QUESTION : " + (msg[i] & 0xff)*256 + (msg[i+1] & 0xff));
	i += 2;

	reponse = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff);
	System.out.println("REPONSE : " + reponse);
	i += 2;

	autorite = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff);
	System.out.println("AUTORITE : " + autorite);
	i += 2;

	additionel = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff);
	System.out.println("ADDITIONEL : " + additionel);
	i += 2;

	// IMPRESSION NOM
	int end =31;
	System.out.print("URL : ");
	i = i+ label.length()+2;
     System.out.println("\nTYPE (HOST ADDRESS) : " + HextoStr(msg[i++]) + ',' + HextoStr(msg[i++]));
	System.out.println("CLASS (INTERNET) : " + HextoStr(msg[i++]) + ',' + HextoStr(msg[i++]));

	// IMPRESSION REPONSES, AUTORITE, INFOS COMPLEMENTAIRES

	System.out.println("\n" + reponse + " Réponse");
	for(j = 0; j <reponse; j++) // on commence à partir de i
	    i = ChampToStr(msg, i);

	System.out.println("\n" + autorite + " Autorité");
	for(j = 0; j < autorite; j++) // on commence à partir de i
	    i = ChampToStr(msg, i);
	
	System.out.println("\n" + additionel + " Additionnel");
	for(j = 0; j < additionel; j++) // on commence à partir de i
	    i = ChampToStr(msg, i);

	    i = 31; 
	  // on va jusqu'au premier champ
	    while((type = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff)) != 1 && type != 6)
		i += (msg[i+8] & 0xff)*256 + (msg[i+9] & 0xff) + 12; 
	    // on va au premier champ de type 1 (contenant une adresse IP)
	    ip=(msg[i+10] & 0xff) + "." + (msg[i+11] & 0xff) + "." + (msg[i+12] & 0xff) + "." +
	    	    (msg[i+13] & 0xff);
	 
		System.out.println("\nL'adresse IP est : " +ip);

	    System.out.println("");
    }



    public static int ChampToStr(byte[] msg, int offset) {
	String s = "";
	int i = offset;
	int RDLength, type;
	//OFFSET
	System.out.println("OFFSET : " + HextoStr(msg[i]) + ',' + 
			HextoStr(msg[i + 1]) + " = " + (msg[i+1] & 0xff));

	
	//NOM
    String nom=label;
	System.out.print("NOM : "+nom);
     i += 2;

	//TYPE
	type = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff);
	System.out.println("\nTYPE : " + HextoStr(msg[i++]) + ',' + HextoStr(msg[i++]));
	//CLASS
	System.out.println("CLASS : " + HextoStr(msg[i++]) + ',' + HextoStr(msg[i++]));
	//TTL
	
	System.out.println("TTL : " +  (msg[i] & 0xff)*16777216 + 
			(msg[i+1] & 0xff)*65536 + (msg[i+2] & 0xff)*256 + (msg[i+3] & 0xff))  ;
	i += 4;

	//RDLENGTH
	RDLength = (msg[i] & 0xff)*256 + (msg[i+1] & 0xff);
	System.out.println("LENGTH : " + RDLength);
	i += 2;
	
	//RDDATA
	
	System.out.println("RDData: "+reponse);
	int j = 0;

	    while(j < RDLength) {
		System.out.print(msg[i+j] & 0xff);
		if(j != RDLength -1)
		    System.out.print(".");
		j++;
	    }
	
	
	   
	i += RDLength;
	
	System.out.println("\n");
	return i;
    }


    public static String HextoStr(int i) {
	String res = Integer.toHexString(i & 0xff);
	if(res.length() == 1)
	    return '0' + res;
	else
	    return res;
    }


   
}