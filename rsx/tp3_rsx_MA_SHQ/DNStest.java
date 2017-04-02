package DNS;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public final class DNStest {

	/**
	 * @param serverAddr DNS address
	 * @param timeOut TTC
	 * @param type of consulting
	 * @param address of consulting
	 * @return
	 */
	public static List<String> search(String serverAddr, int timeOut, 
			String type, String address) {
		InitialDirContext context = null;
		List<String> resultList = new ArrayList<String>();
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put("java.naming.factory.initial",
					"com.sun.jndi.dns.DnsContextFactory");
			env.put("java.naming.provider.url", "dns://" + serverAddr + "/");
			env.put("com.sun.jndi.ldap.read.timeout", String.valueOf(timeOut));
			context = new InitialDirContext(env);
			String dns_attrs[] = { type };

			Attributes attrs = context.getAttributes(address, dns_attrs);
			Attribute attr = attrs.get(type);
			if (attr != null) {
				for (int i = 0; i < attr.size(); i++) {
					resultList.add((String) attr.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(context!=null){
				try {
					context.close();
				} catch (NamingException e) {
					
				}
			}
		}
		return resultList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		List<String> resultList = search("193.49.225.90", 2000, "A","www.lifl.fr");
		for(String str : resultList){
			System.out.println(str);
		}
	}


}