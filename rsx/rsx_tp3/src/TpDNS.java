


import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.lang.String;

/**
 * Classe utilisée pour envoyer des requêtes DNS, puis analyser/imprimer et leurs résultats.
 */
public class TpDNS {
    
    public static void main (String[] args) throws Exception {
	
	/*Serveur de Google, pour tester chez soi*/
	InetAddress dst = InetAddress.getByName("8.8.8.8");
	String address="www.lifl.fr";
	/*Serveur de lifl, pour tester à l'université*/
	//InetAddress dst = InetAddress.getByName("172.18.12.9");
	DatagramSocket ds;
	DatagramPacket dp;
	DatagramPacket dp_receive = new DatagramPacket(new byte[512], 512);
	int port = 53;//　53Port為DNS（Domain Name Server，功能變數名稱伺服器）伺服器所開放，
			//主要用於功能變數名稱解析，DNS服務在NT系統中使用的最為廣泛。
	byte[] message = createRequest(address);
			dp = new DatagramPacket(message, message.length, dst, port);
			ds = new DatagramSocket();
			ds.send(dp);//发送
			ds.receive(dp_receive);//接受
			byte[] messageRec = dp_receive.getData();
		
			for(int i = 0; i < dp_receive.getLength(); i++) {
			    System.out.print("," + Integer.toHexString(messageRec[i]));//hex:十六进制
			    if(i % 16 == 0)
				System.out.println("");
			}
			System.out.println("");
          printPacket(dp_receive);

			int i=0, ip;
		
			i = 31; // on va jusqu'au premier champ
			while(getShortValue(messageRec, i) != 1)
			    i += getShortValue(messageRec, i+8) + 12; // on va au premier champ de type 1 (contenant une adresse IP)

			// CODAGE DES 4 OCTETS DE L'IP DANS UN SEUL INT (qui est codé sur 4 octets)
			ip = (messageRec[i+10] & 0xff)*16777216 + (messageRec[i+11] & 0xff)*65536 +
					(messageRec[i+12] & 0xff)*256 + (messageRec[i+13] & 0xff);
 System.out.println("\n\nAdresse IP sous forme d'int : " + ip);
		    }
		    

	
	// Q5 (RENVOI DE L'ADRESSE IP SOUS FORME D'INT
	
    

    /**
     * Envoie une requête DNS à propos d'une URL et imprime le contenu du paquet reçu ainsi que l'adresse IP correspondante
     * (Question 4)
     * @param label l'URL dont on veut connaître les informations
     * @param dst l'adresse à laquelle on envoie la requête
     */ 
  
    public static byte[] createRequest(String address) {
	int length = 18 + address.length();
	int i;
	byte[] requestInByte = new byte[length];
	// DECOUPAGE DE LA CHAINE EN SOUS CHAINES
	String[] addressSplit = address.split("[.]"); // "." = regexp pour le point "."

	// REMPLISSAGE DU SQUELETTE DE REQUETE
	requestInByte[0] = (byte) 0x08;
	requestInByte[1] = (byte) 0xbb;
	requestInByte[2] = requestInByte[5] = requestInByte[length -1] = requestInByte[length - 3] = (byte) 0x01;
	requestInByte[3] = requestInByte[4] = requestInByte[length -2] = requestInByte[length - 4] = requestInByte[length - 5] = (byte) 0;
	for(i = 6; i < 12; i++)
		requestInByte[i] = (byte) 0;

	// AJOUT DU LABEL 
	for(String str : addressSplit) {
		requestInByte[i++] = (byte) (str.length() & 0xff); 
	    
	    //AJOUT DE LA LONGUEUR A LA PLACE DU POINT
	    for(char c : str.toCharArray())
	    	requestInByte[i++] = (byte) c; //AJOUT DES CARACTERES
	}
	return requestInByte;
    }

    /**
     * Imprime et décrypte le contenu d'un paquet
     * Cette fonction utilise printParamStr et printChampStr
     * (Pour la question 4)
     * @param packet le paquet qu'on veut imprimer
     */
    public static void printPacket(DatagramPacket packet) {
	byte[] msg = packet.getData();
	int length = packet.getLength();
	int i = 0;
	int j, offset, nbchar, ip, finChaine, nbRep, nbAut, nbAdd, type;

	System.out.println("IDENTIFIANT : " + Integer.toHexString(msg[i]) + "," + Integer.toHexString(msg[i]));

	printParamToString(getShortValue(msg, i)); // IMPRESSION PARAMETRES
	i += 2;

	System.out.println("QUESTION : " + getShortValue(msg, i));
	i += 2;

	nbRep = getShortValue(msg, i);
	System.out.println("REPONSE : " + nbRep);
	i += 2;

	nbAut = getShortValue(msg, i);
	System.out.println("AUTORITE : " + nbAut);
	i += 2;

	nbAdd = getShortValue(msg, i);
	System.out.println("INFOS COMPLEMENTAIRES : " + nbAdd);
	i += 2;

	// IMPRESSION NOM
	finChaine = 31;
	System.out.print("URL : ");
	
	i = printPacketString(msg, i) + 1;
	    //for(; i < finChaine; i++)
	    // System.out.print((char) msg[i]);

	System.out.println("\nTYPE (HOST ADDRESS) : " + Integer.toHexString(msg[i++]) + ',' + Integer.toHexString(msg[i++]));
	System.out.println("CLASS (INTERNET) : " + Integer.toHexString(msg[i++]) + ',' + Integer.toHexString(msg[i++]));

	// IMPRESSION REPONSES, AUTORITE, INFOS COMPLEMENTAIRES

	System.out.println("\n" + nbRep + " Réponses");
	for(j = 0; j < nbRep; j++) // on commence à partir de i
	    i = printChampStr(msg, i);

	System.out.println("\n" + nbAut + " Autorités");
	for(j = 0; j < nbAut; j++) // on commence à partir de i
	    i = printChampStr(msg, i);
	
	System.out.println("\n" + nbAdd + " Informations Additionelles");
	for(j = 0; j < nbAdd; j++) // on commence à partir de i
	    i = printChampStr(msg, i);

	// on récupère l'indice où on s'est arreté après avoir lu un champ pour recommencer à partir de cet indice

	// IMPRESSION ADRESSE IP
	    i = 31; // on va jusqu'au premier champ
	    while((type = getShortValue(msg, i)) != 1 && type != 6)
		i += getShortValue(msg, i+8) + 12; // on va au premier champ de type 1 (contenant une adresse IP)
	    if(type == 1)
		System.out.println("\nL'adresse IP est : " + (msg[i+10] & 0xff) + "." + (msg[i+11] & 0xff) + "." + (msg[i+12] & 0xff) + "." + (msg[i+13] & 0xff));
	   
	    System.out.println("");
    }

    /**
     * Imprime un champ réponse/autorité/additionnel d'un paquet
     * (Pour la question 4)
     * @param msg le tableau d'octets correspondant au paquet reçu
     * @param offset l'indice de début du champ
     * @return l'indice de début du prochain champ
     */
    public static int printChampStr(byte[] msg, int offset) {
	String s = "";
	int i = offset;
	int rdLength, type;
	//OFFSET
	System.out.println("OFFSET : " + Integer.toHexString(msg[i]) + ',' + Integer.toHexString(msg[i+1]) + " = " + (msg[i+1] & 0xff));
	//NOM
	System.out.print("NOM : ");

	i = printPacketString(msg, i);
	
	//TYPE
	type = getShortValue(msg, i);
	System.out.println("\nTYPE : " + Integer.toHexString(msg[i++]) + ',' + Integer.toHexString(msg[i++]));
	//CLASS
	System.out.println("CLASS : " + Integer.toHexString(msg[i++]) + ',' + Integer.toHexString(msg[i++]));
	//TTL
	System.out.println("TTL : " + getIntValue(msg, i));

	i += 4;
	
	
	
	
	

	//RDLENGTH
	rdLength = getShortValue(msg, i);
	System.out.println("LENGTH : " + rdLength);
	i += 2;
	
	//RDDATA
	int j = 0;
	switch(type) {
	case 1: // AFFICHAGE ADRESSE IP
	    while(j < rdLength) {
		System.out.print(msg[i+j] & 0xff);
		if(j != rdLength -1)
		    System.out.print(".");
		j++;
	    }
	    break;
	case 6: // AFFICHAGE SERVEUR MAITRE DU DOMAINE
	    printPacketType6(msg, i + j, rdLength);
	    break;
	default:
	    printPacketRDData(msg, i + j, rdLength);
	}    
	i += rdLength; // on ajoute à i les octets parcourus
	
	System.out.println("\n");
	return i;
    }

    /**
     * Imprime le champ de paramètres d'un paquet
     * (Pour la question 4)
     * @param i les paramètres sous forme d'entier
     */
    public static void printParamToString(int i) {	
	String s = "";
	int r = i;
	for(int k=0;k<16;k++) {
		s=Integer.toBinaryString(r)+s;
    }
	
	char[] b = s.toCharArray();
	int j;
	System.out.println("    QR : " + b[0]);
	System.out.print("    OPCODE : ");
	for(j = 1; j < 5; j++)
	    System.out.print(b[j]);
	System.out.println("\n    AA : " + b[5]);
	System.out.println("    TC :" + b[6]);
	System.out.println("    RD : " + b[7]);
	System.out.println("    RA : " + b[8]);
	System.out.println("    UNUSED : " + b[9]);
	System.out.println("    AD :" + b[10]);
	System.out.println("    CD :" + b[11]);
	System.out.print("    RCODE :");
	for(j = 12; j < 16; j++)
	    System.out.print(b[j]);
	System.out.println("");
    }

    /**
     * Permet de récupérer une valeur "short" (codée sur 2 octets) à partir dans un tableau de bytes à partir d'un offset i.
     * t[i] étant l'octet de poids fort
     * @param t le tableau de bytes
     * @param i l'offset
     * @return les 2 octets suivant i, réunis en un int
     */
    public static int getShortValue(byte[] t, int i) {
	return (t[i] & 0xff)*256 + (t[i+1] & 0xff);
    }

    /**
     * Permet de récupérer un entier (codé sur 4 octets) à partir dans un tableau de bytes à partir d'un offset i.
     * t[i] étant l'octet de poids fort
     * @param t le tableau de bytes
     * @param i l'offset
     * @return les 4 octets suivant i, réunis en un int
     */
    public static int getIntValue(byte[] t, int i) {
	return (t[i] & 0xff)*16777216 + (t[i+1] & 0xff)*65536 + (t[i+2] & 0xff)*256 + (t[i+3] & 0xff);
    }

    /**
     * Permet de connaitre où s'arrête la première chaine de caractère d'un paquet
     * @param t un paquet
     * @param i un entier
     * @return l'indice de fin de la chaine
     */
 


    public static int printPacketType6(byte[] msg, int offset, int rdLength) {
	int i = offset;
	int start = msg[i] & 0xff;
	while(i < offset + rdLength - 16) {
	    start = msg[i] & 0xff;
	    switch(start) {
	    case 0 :
		i += 1;
		break;
	    case 0xc0 :
		if(msg[printPacketString(msg, (msg[i+1] & 0xff))] == 0)
		    System.out.print(" ");
		i += 2;
		break;
	    default :
	    	int length = msg[i++] & 0xff;
	    	while(i <= offset + length) {
	    	    System.out.print((char) (msg[i++] & 0xff));
	    	}
	    }
	}
	for(i = offset + rdLength - 16; i < offset + rdLength; i += 4)
	    System.out.print(getIntValue(msg, i) + " ");
	return i;
    }	
    
    public static int printPacketRDData(byte[] msg, int offset, int rdLength) {
	int i = offset;
	int start = msg[i] & 0xff;
	while(i < offset + rdLength) {
	    start = msg[i] & 0xff;
	    switch(start) {
	    case 0 :
		i += 1;
		break;
	    case 0xc0 :
		printPacketString(msg, (msg[i+1] & 0xff));
		i += 2;
		break;
	    default :
	    	int length = msg[i++] & 0xff;
	    	while(i <= offset + length) {
	    	    System.out.print((char) (msg[i++] & 0xff));
	    	}
	    }
	}
	return i;
    }	
    
    public static int printPacketString(byte[] msg, int offset) {
	int i = offset;
	int start = msg[i] & 0xff;
       	while((msg[i] & 0xff) != 0) {
	    start = msg[i] & 0xff;
	    switch(start) {
	    case 0 :
		i += 1; 
		break;
	    case 0xc0 :
		if(msg[printPacketString(msg, (msg[i+1] & 0xff))] == 0)
		    return i + 2;
		i += 2;
		break;
	    default :
	    	int length = msg[i++] & 0xff;
	    	while(i <= offset + length) {
	    	    System.out.print((char) (msg[i++] & 0xff));
	    	}
		
	    }
	}
	return i;
    }

   

   
}

