


public class simpleppp {
	
	public static byte[] createRequestData(String request) {
		int totalLength = 18 + request.length();
		int i;
		byte[] requestInByte = new byte[totalLength];
		
		//Decoupage de la chaine
		String[] splitRequest = request.split("[.]");
		
		//Remplissage du squelette de la requête
		requestInByte[0] = (byte) 0x08;
		requestInByte[1] = (byte) 0xbb;
		requestInByte[2] = requestInByte[5] = requestInByte[totalLength - 1] 
				= requestInByte[totalLength - 3] = (byte) 0x01;
		requestInByte[3] = requestInByte[4] = requestInByte[totalLength - 2] 
				= requestInByte[totalLength - 4] = requestInByte[totalLength - 5] = (byte) 0;
		for (i = 6; i <= 11; i++){
			requestInByte[i] = (byte) 0;
		}
		
		for (String str : splitRequest) {
			requestInByte[i++] = (byte) (str.length() & 0xff);
			
			for (char c : str.toCharArray())
				requestInByte[i++] = (byte) c;		
		}
		
		return requestInByte;
	}

}