/** UUIDUtil.java created by jinhao
 * on 2007-12-3
 * with BBSCrawler
 */

package cn.engine.utils;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.apache.log4j.Logger;


public class UUIDUtil {
	private static final Logger logger = Logger.getLogger(UUIDUtil.class);
	public static HashSet uuidSet = new HashSet();
	
	public static void main(String []args){
		System.out.println(getMd5IdUseUrl("http://news.sohu.com/20120702/n347066330.shtml"));
		System.out.println(getMd5IdUseUrl("http://news.sohu.com/20120702/n347066330.shtml"));
		System.out.println(getMd5IdUseUrl("http://news.sohu.com/20120703/n347066330.shtml"));
//		int i=0;
//		for(;i<10000;i++){
//			Thread temp = new UuidTest();
//			temp.start();
//			try {
//				temp.sleep(49);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			temp.stop();
//		}
//		System.out.println("i="+i+" uuidSet finished！size:"+UUIDUtil.uuidSet.size());
	}
	
	
	/**
	 * 获取字符串的md5
	 * 
	 * @param inStr
	 * @return
	 */
	public static String getMd5IdUseUrl(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = md5Bytes[i] & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	
	

	public synchronized static String getUUID(String url){
		String uuid = getMd5IdUseUrl(url);;
//		if(uuidSet.contains(uuid))
//		{
//			logger.info("重复:"+uuid);
//		}else{
//			uuidSet.add(uuid);
//			logger.info("uuid------"+uuid);
//		}
		
		return uuid;
	}
	
	public synchronized static String getUUID(){
		
		String uuid = UUID.randomUUID().toString().replace("-", "");
//		if(uuidSet.contains(uuid))
//		{
//			logger.info("重复:"+uuid);
//		}else{
//			uuidSet.add(uuid);
//			logger.info("uuid------"+uuid);
//		}
		
		return uuid;
	}
}
