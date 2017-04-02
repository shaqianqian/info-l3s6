package dns;

import java.io.IOException;
import java.net.*;


public class QueryDNS2 {
       
   static byte dns_query[];
   int secureNumberOfLoop = 0;
   DatagramSocket ds;
   DatagramPacket dp;
   DatagramPacket dpr;
   int port;
   InetAddress address;
   int buffer_size = 1024;
   InetAddress receiverAddress;
   
   public QueryDNS2(String s){
       this.port = 53;

       try {
               ds = new DatagramSocket();
       } catch (SocketException e) {
               e.printStackTrace();
       }
       
       try {
               receiverAddress = InetAddress.getByName("172.18.12.9");
       } catch (UnknownHostException e) {
               e.printStackTrace();
       }
       dns_query = buildDnsQuestion(s);
   }
   
   public byte[] buildDnsQuestion(String s)
   {
	   byte[] dnsQuerry = new byte[18+s.length()];
	   
	   dnsQuerry[0] =(byte)0x08; // id
	   dnsQuerry[1] =(byte)0xbb;
	   dnsQuerry[2] =(byte)0x01; // param
	   dnsQuerry[3] =(byte)0x00;
	   dnsQuerry[4] =(byte)0x00; // ques
	   dnsQuerry[5] =(byte)0x01;
	   dnsQuerry[6] =(byte)0x00; // rep
	   dnsQuerry[7] =(byte)0x00;
	   dnsQuerry[8] =(byte)0x00; // autorite
	   dnsQuerry[9] =(byte)0x00;
	   dnsQuerry[10] =(byte)0x00; // info complem
	   dnsQuerry[11] =(byte)0x00;
	
	   int indexQuerry=12; // the index of the next value
	   int indexWord=0;    // the index of the adress
	   int i = 0;			// the length between each point.
	   
	   while(indexWord < s.length())
	   {
		   char c = s.charAt(indexWord);
		   if (c != '.')
		   {
			   dnsQuerry[indexQuerry+1] = (byte)c; //+1 for the value of the next word

			   // System.out.print("dnsQ : "+(indexQuerry+1)+" -> "+(byte)c+" -:- ");
			   i++;
		   }
		   else
		   {
			   dnsQuerry[indexQuerry-i] = (byte)i; // -i to go to the value of the next word
			   
			  // System.out.println((byte)i+"  at : "+(indexQuerry-i));
			   i=0;
		   }
		   indexWord++;
		   indexQuerry++;
	   }
	   dnsQuerry[indexQuerry-i] = (byte)i; // -i one last time to put the value of the last word
	 
	   dnsQuerry[indexQuerry+1] =(byte)0x00;
	   dnsQuerry[indexQuerry+2] =(byte)0x00; //type
	   dnsQuerry[indexQuerry+3] =(byte)0x01;
	   dnsQuerry[indexQuerry+4] =(byte)0x00; //class
	   dnsQuerry[indexQuerry+5] =(byte)0x01;
	   
	   return dnsQuerry;
   }

   public static int byteToInt(byte[] b, int i)	{ return ((int)b[i])&0xff; }
   
   public static int shortToInt(byte[] b, int i) { return byteToInt(b,i)*256+byteToInt(b,i+1); }
   
   public InetAddress byteToIpAddress(byte[] b, int i)
   {
	   byte[] add = new byte[4];
	   
	   for(int j=0;j<4;j++)
		   add[j] = b[i+j];
	   
	   try {
		   return InetAddress.getByAddress(add);
	   } catch (UnknownHostException e) {
		   e.printStackTrace();
	   }
	   return null;
   }
   
   public int byteToString(byte[] b, int i)
   {
	   if (++secureNumberOfLoop > 4000)
	   {
		   System.err.println("Numbre of Loop to high (>4000) might be a security problem");
		   System.exit(0);
	   }
	   
	   int code;
	   while ((code = byteToInt(b, i)) > 0)
	   {
		   /*si ce code est une longueur et donc une chaine a lire octet par octet*/
		   if(code < 192) // si commence par 11 (min 0xc)
		   {
			   System.out.print(".");
			   /* affichage des bytes de b[i+1] a b[i+code]*/
			   for (int ind = i+1; ind<=i+code;ind++)
				  System.out.print((char)b[ind]);
			   
			   i+=code+1;
		   }
		   else /* c'est un pointeur donc 2 bite de poid fort a 1 (11??????????????) -> faire un & 0x3fff  pour recuperer l'offset*/
		   {
			   int offset = shortToInt(b, i)& 0x3fff;
			   byteToString(b, offset);
			   i++;
			   break;
		   }
	   }
	   return i;
   }
   
   public String getClass(byte[] b, int i)
   {
	   switch (shortToInt(b, i)) 
	   {
			case 1:
				return "IN";//	: the Internet";
			case 2:
				return "CS";//	: the CSNET class (Obsolete - used only for examples in some obsolete RFCs)";
			case 3:
				return "CH";//	: the CHAOS class";
			case 4:
				return "HS";//	: Hesiod [Dyer 87]";
			case 255:
				return "*";//	: any class";
			default:
				return "----ERROR----";
		}
   }
   
   public String getType(byte[] b, int i)
   {
	   switch (shortToInt(b, i)) 
	   {
			case 1:
				return "A";//		: a host address";
			case 2:
				return "NS";//		: an authoritative name server";
			case 3:
				return "MD";//		: a mail destination (Obsolete - use MX)";
			case 4:
				return "MF";//		: a mail forwarder (Obsolete - use MX)";
			case 5:
				return "CNAME";//	: the canonical name for an alias";
			case 6:
				return "SOA";//		: marks the start of a zone of authority";
			case 7:
				return "MB";//		: a mailbox domain name (EXPERIMENTAL)";
			case 8:
				return "MG";//		: a mail group member (EXPERIMENTAL)";
			case 9:
				return "MR";//		: a mail rename domain name (EXPERIMENTAL)";
			case 10:
				return "NULL";//	: a null RR (EXPERIMENTAL)";
			case 11:
				return "WKS1";//	: a well known service description";
			case 12:
				return "PTR";//		: a domain name pointer";
			case 13:
				return "HINFO";//	: host information";
			case 14:
				return "MINFO";//	: mailbox or mail list information";
			case 15:
				return "MX";//		: mail exchange";
			case 16:
				return "TXT";//		: text strings";
			case 252:
				return "AXFR";//	: A request for a transfer of an entire zone";
			case 253:
				return "MAILB";//	: A request for mailbox-related records (MB, MG or MR)";
			case 254:
				return "MAILA";//	: A request for mail agent RRs (Obsolete - see MX)";
			case 255:
				return "*";//		: A request for all records";
			default:
				return "----ERROR----";
		}
   }
   
   public int getTTL(byte[] b, int i)
   {
	   return byteToInt(b,i)*256*256*256+byteToInt(b,i+1)*256*256+byteToInt(b,i+2)*256+byteToInt(b,i+3);
   }
   
   protected void sendQueryDNS(){
	   if(ds != null ){
	       byte[] buffer = dns_query;
	       dp = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
	       try {
	               ds.send(dp);
	       } catch (IOException e) {
	               e.printStackTrace();
	       }
	   }
   }
       
   protected void receive()
   {
	   byte[] buffer = new byte[1024];
	   dpr = new DatagramPacket(buffer, buffer.length);
	   
	   try {
	           ds.receive(dpr);
	   } catch (IOException e) {
	           e.printStackTrace();
	   }
	   
	   
	   System.out.print("identifiant : \t\t"+shortToInt(dpr.getData(),0)+"\t");
	   System.out.println("parametre : \t\t"+shortToInt(dpr.getData(),2));
	   int nbQues = shortToInt(dpr.getData(),4);
	   System.out.print("nombre de question : \t"+nbQues+"\t");
	   int nbRep = shortToInt(dpr.getData(),6);
	   System.out.println("nombre de reponse : \t"+nbRep);
	   int nbAut = shortToInt(dpr.getData(),8);
	   System.out.print("nombre d'autorite : \t"+nbAut+"\t");
	   int nbAdd = shortToInt(dpr.getData(),10);
	   System.out.println("nombre d'additionnel : \t"+nbAdd);
	   
	   
	   
	   int index = 12;
	   
	   System.out.println("==================");
	   for (int i =1;i<=nbQues;i++)
	   {
		   System.out.print("question n° "+i+" : ");
		   index = byteToString(dpr.getData(),index)+1;
		   System.out.print("\tClasse : "+getType(dpr.getData(),index));
		   index+=2;
		   System.out.println("\tType : "+getClass(dpr.getData(),index));
		   index+=2;
	   }
	   
	   int dataLength;
	   
	   byte[] data= dpr.getData();
	   
	   System.out.println("==================");
	   for (int i=1; i <= nbRep+nbAut+nbAdd; i++)
	   {
		   if (i<=nbRep) System.out.println("Reponse n° "+i+": ");
		   else if ((i-nbRep)<=nbAut) System.out.println("Autorite n° "+(i-nbRep)+": ");
		   else System.out.println("Additionnel n° "+(i-nbRep-nbAut)+": ");
		   
		   System.out.print("\tNom : ");
		   index = byteToString(data,index)+1;
		   
		   String type = getType(data,index);
		   System.out.print("\n\tType : "+type);
		   index+=2;
		   
		   System.out.println("\tClass : "+getClass(data,index));
		   index+=2;
		   
		   System.out.print("\tTTL : "+getTTL(data,index));
		   index+=4;
		   
		   dataLength = shortToInt(data,index);
		   System.out.println("\tRDLength : "+dataLength);
		   index+=2;
		   
		   /* a voir, il faut en fonction de la reponse donne traite les donnees differements*/
		   System.out.print("\tData : ");
		   /*for (int j=index;j<index+dataLength;j++) // affiche les donne en suite d'octet
		   {
			   Formatter f = new Formatter();
			   System.out.print(f.format( "0x%1$02x, ", dpr.getData()[j]<0?256+dpr.getData()[j]:dpr.getData()[j]));
		   }*/
		   if (dataLength == 4 && type=="A")
			   System.out.println(byteToIpAddress(data,index).getHostAddress());
		   else 
			   byteToString(data,index);
		   
		   index += dataLength;
		   System.out.println();
	   }
   }
   
   public static void main(String[] args) 
   {
	   QueryDNS2 test = new QueryDNS2("www.lifl.fr"); 
	   
	   test.sendQueryDNS();
	   while(true)
		   test.receive();
   }
}

