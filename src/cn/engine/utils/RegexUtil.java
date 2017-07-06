package cn.engine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 正则匹配、替换工具类
 * @author Administrator
 *
 */
public class RegexUtil {
	private Logger log=Logger.getLogger(this.getClass());
	
	/**
	 * 正则判断是否匹配
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static boolean isMatcherRegex(String str, String strPattern){
		if(strPattern==null||strPattern.trim().equals("")){
			return false;
		}
//		System.out.println(str);
		str=str.trim();
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	/**
	 * 返回正则匹配结果
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static String getMatcherRegex(String str, String strPattern) {
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		String result=null;
		while(m.find()){
			result=m.group(0);
			/**取最后一个匹配项，百度网页解析第一个匹配项不是时间 */
			//break;
		}
		return result;
	}
	/**
	 * 汉字关键字绝对匹配,如果完全匹配,返回true,不能完全匹配返回FALSE
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static boolean getCompleteMatcher(String str, String strPattern) {
		if(str==null||str.length()==0||strPattern==null||strPattern.length()==0){
			return false;
		}
		boolean checked = false;
		if(strPattern.indexOf(str)<0){
			return false;
		}else{
			return true;
		}
//		String[] sb=strPattern.split("\\|");
//		for(int i=0;i<sb.length;i++){
//			Pattern p = Pattern.compile(sb[i]);
//			Matcher m = p.matcher(str);
//			if(m.find()){
//				checked = true;
//				break;
//			}
//		}
//		return checked;
	}
	
	/**
	 * 获得正则组匹配的结果列表集合
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static List<String> getAllMatcherListRegex(String str, String strPattern){
		int matchNum=0;
		
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		List<String> result=new ArrayList<String>();
		while(m.find()){
			matchNum++;
			result.add(m.group(0));
			str=str.substring(m.end(0));
			m=p.matcher(str);
		}
		return result;
	}
	
	/**
	 * 获得正则的全部匹配结果
	 * @param plainText
	 * @param strPattern
	 * @return
	 */
	public static String getAllMatchRegex(String str, String strPattern){
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(str);
		String result="";
		while(m.find()){
			result=m.group(0);
		}
		return result;
	}
	
	/**
	 * 获得正则的第一个的组匹配结果
	 * @param plainText
	 * @param strPattern
	 * @return
	 */
	public static String getMatchGroupRegex(String str, String strPattern){
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		String result="";
		if(m.find()){
			result = m.group(1);
		}
		return result;
	}
	
	/**
	 * 获得正则的第一个的组匹配结果
	 * @param plainText
	 * @param strPattern
	 * @return
	 */
	public static String getMatchGroupRegex(String str, String strPattern,int group){
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		String result="";
		if(m.find()){
			result = m.group(group);
		}
		return result;
	}
	
	/**
	 * 正则添加
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static String addRegex(String str, String strPattern, String strAppend){
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		
		while(m.find()){
			m.appendReplacement(sb, m.group()+strAppend);
		}
		m.appendTail(sb);
		return sb.toString();

	}
	
	
	/**
	 * 正则替换
	 * @param str
	 * @param strPattern
	 * @return
	 */
	public static String replaceRegex(String str, String strPattern){
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		String result=null;
		if(m.find()){
			result=m.replaceAll("");
		}else{
			result=str;
		}
		return result;
	}
	
	
	/**
	 * @param nub 1.文字匹配 2.url匹配 3.正则匹配
	 * @param text
	 * @param newsName
	 * @param newsURL
	 * @return
	 */
	public static boolean make(int blockParserType,String text,String newsName,String newsURL){
		if(blockParserType == 1){
			return textMake(text,newsName);
		}else if(blockParserType == 2){
			return urlMake(text,newsURL);
		}else if(blockParserType == 3){
			return patternMake(text,newsName,newsURL);
		}else{
			return true;
		}
	}
	
	private static boolean textMake(String text,String newsName){
		Pattern p = Pattern.compile(text);
		Matcher m = p.matcher(newsName);
		return m.find();
	}
	
	private static boolean urlMake(String value,String newsURL){
		Pattern p = Pattern.compile(value);
		Matcher m = p.matcher(newsURL);
		return m.find();
	}
	
	private static boolean patternMake(String str,String newsName,String newsURL){
		Pattern p = Pattern.compile(str);
		Matcher m1 = p.matcher(newsName);
		Matcher m2 = p.matcher(newsURL);
		if(m1.find()){
			return true;
		}else{
			return m2.find();
		}
	}

//	public static List<String> getAllMatcherListRegexGroup(String str,
//			String strPattern) {
//        int matchNum=0;
//		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
//		Matcher m = p.matcher(str);
//		List<String> result=new ArrayList<String>();
//		while(m.find()){
//			matchNum++;
//			result.add(m.group(0));
////			result.add(m.group(1));
////			str=str.substring(m.end(0));
////			m=p.matcher(str);
//		}
//		return result;
//	}
	
	public static List<String> getAllMatcherListRegexGroup1(String str,
			String strPattern,int gourp) {
        int matchNum=0;
		Pattern p = Pattern.compile(strPattern,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		List<String> result=new ArrayList<String>();
		while(m.find()){
			matchNum++;
			result.add(m.group(gourp));
//			result.add(m.group(1));
//			str=str.substring(m.end(0));
//			m=p.matcher(str);
		}
		return result;
	}
}
