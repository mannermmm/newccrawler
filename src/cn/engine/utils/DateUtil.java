package cn.engine.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author liangyanyuan
 * 用于处理日期字符串的工具类,返回date
 * 非法的日期将一律返回null,暂时不具有判断日期字符串是否合法的功能
 * 本类根据传入的参数文本内容,进行全文匹配,把所有符合要求的时间类型匹配出来,转化为date类型,
 * 可以获得排序后的时间数组,DateUtil.extractDate(String text)
 * 也可以获得不早于当前时间的最早时间DateUtil.getPostDate(String text)
 * */
public class DateUtil {
	//匹配09-07-09 12:33|07-09 12:33|7-7 1:4|2009-9-11 3:33 DateFormat格式:yy-MM-dd HH:mm
	public static String pattern_num1="(\\d{1,2}[-\\/\\._]){1,2}\\d{1,2}\\s*(\\d{1,2}:\\d{1,2})";
	//匹配11/9/2009 HH:mm,日期格式为:dd-MM-yy HH:mm
	public static String pattern_num2="(\\d{1,2}[-\\/\\._]){2}\\d{4}\\s*(\\d{1,2}:\\d{1,2})";
	//匹配09-02-2010 12:33
	public static String pattern_num3="(\\d{2}[-/_.]){2}\\d{4}\\s+\\d{2}:\\d{2}";
	//匹配Wed Jul  8 17:09:05 2009|May 27 23:24
	public static String pattern_en1="(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+(\\d{1,2})\\s+(\\d{1,2}:\\d{1,2})(:\\d{1,2}){0,1}(\\s+\\d{4}){0,1}";
    //匹配Thu 6 Jan 2005 18:44:56
	public static String pattern_en2="((Sun|Mon|Tue|Wed|Thu|Fri|Sat)\\s+){0,1}\\d{1,2}\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{4}\\s+(\\d{1,2}:\\d{1,2})(:\\d{1,2}){0,1}";
	
	//匹配2009年9月11号 7时33分
	public static String pattern_cn="((\\d{4})年){0,1}(\\d{1,2})月(\\d{1,2})[号日\\s]\\s*((\\d{1,2})[时\\s:](\\d{1,2})分{0,1}){0,1}";
	
	/** 匹配 2010-9-12, 2010-9-12 14:35, 2010-9-12 14:35:00 */
	public static String pattern_metaPostDate = "\\d{2,4}[年/-]\\d{1,2}[月/-]\\d{1,2}[日]{0,1}" +
							"(\\s+\\d{1,2}([:]\\d{1,2}){1,2}){0,1}";
	/** 论坛时间至少要包含时 */
	public static String pattern_BBSPostDate = 
		"\\d{2,4}[年/-]\\d{1,2}[月/-]\\d{1,2}[日]{0,1}" +
		"["+ StringUtil.BLANK_REGEX + "]+" + 
		"\\d{1,2}([:]\\d{1,2}){1,2}";
	
	//匹配 新闻发布日期
	public static String pattern_newsPostDate="(\\d{4}[年-]\\d{1,2}[月-]\\d{1,2}[号日\\s]*\\d{0,2}[时\\s:]*\\d{0,2}[分\\s:]*\\d{0,2})";
	
	public static HashMap<String,String> patternMap=new HashMap<String,String>();
	static { initMap();}
	
	public static  SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 初始化patternMap,为每一类日期格式设置相应的日期转换格式
	 * */
	private static void initMap(){
		patternMap.put(pattern_num1,"yy-MM-dd HH:mm");
		patternMap.put(pattern_num2,"dd-MM-yy HH:mm");
		patternMap.put(pattern_en1, "MMM dd HH:mm yyyy");
		patternMap.put(pattern_en2, "EEE dd MMM yyyy HH:mm");
		patternMap.put(pattern_cn, "yyyy-MM-dd HH:mm");
		patternMap.put(pattern_newsPostDate, "yyyy-MM-dd HH:mm");
		patternMap.put(pattern_num3, "MM-dd-yyyy HH:mm");
		patternMap.put(pattern_metaPostDate, "");
	}
	
	/**
	 * 把pattern_num1匹配到得日期字符串格式化为标准格式:yy-MM-dd HH:mm,
	 * 没有年份的默认添加当前年份,没有时间的默认添加09:30
	 * 
	 * */
	private static Date format_num1(String regexStr,String pattern){
		regexStr=regexStr.trim();
		String result= "";
		regexStr.replaceAll("[-\\/\\._]", "-");//把日期中的分隔符转化为统一的"-";
		String dateStr;//日期,如09-9-11或者9-11
		String timeStr;//时间,如
		try{
			//判断是否有时间
			if(regexStr.indexOf(":")>0){//有时间
				dateStr=regexStr.split(" ")[0];
				timeStr=regexStr.split(" ")[1];
			}else{//没有时间--!
				dateStr=regexStr;	
				timeStr="09:30";//添加默认时间--!
				//str=dateStr+" "+timeStr;
			}
			//判断是否有年份
			if(dateStr.split("-").length<=2){//没年份
				dateStr=getCurrentYear().substring(2)+"-"+dateStr;
			}
			result=dateStr+" "+timeStr;
		}catch(ArrayIndexOutOfBoundsException ax){
			result = "";
			//System.out.println("*****************时间错误regexStr: " + regexStr);
		}
		return parseDate(result,pattern);
	}
	
	/**
	 * 把pattern_num2匹配到得日期字符串格式化为标准格式:dd/MM/yyyy HH:mm,
	 * 没有年份的默认添加当前年份,没有时间的默认添加09:30
	 * 
	 * */
	private static Date format_num2(String regexStr,String pattern){
		regexStr=regexStr.trim();
		regexStr.replaceAll("[-\\/\\._]", "-");//把日期中的分隔符转化为统一的"-";
		String dateStr;//日期,如09-9-11或者9-11
		String timeStr;//时间,如
		//判断是否有时间
		if(regexStr.indexOf(":")<0){//有时间
			dateStr=regexStr.split(" ")[0];
			timeStr=regexStr.split(" ")[1];
		}else{//没有时间--!
			dateStr=regexStr;	
			timeStr="09:30";//添加默认时间--!
			//str=dateStr+" "+timeStr;
		}
		//判断是否有年份
		if(dateStr.split("-").length<=2){//没有年份
			dateStr=dateStr+"-"+getCurrentYear();
		}
		String result=dateStr+" "+timeStr;
		
		return parseDate(result,pattern);
	}
	
	
	/**
	 * 目前对于Wed Jul 8 17:09:05 2009这种格式的日期,simpleDateFormat无法转换,只能
	 * 用过期地方法new Date(String s),但是格式必须完整,不能缺少年份,此方法就是要做处理
	 * */
	private static Date format_en1(String regexStr,String pattern){
		regexStr=regexStr.trim();
		String result=null;
		String[] strArray=regexStr.split(" ");
		int length=strArray.length;
		if(strArray[length-1].indexOf(":")>0){//没有年份
		   result=regexStr+" "+getCurrentYear();//添加当前年份
		}
		
		result=regexStr;
//		Calendar c=Calendar.getInstance();
//		
//		return parseDate(result,pattern);
		return new Date(result);
	}
	
	/**
	 * 目前对于Thu 6 Jan 2005 18:44:56这种格式的日期,simpleDateFormat无法转换,只能
	 * 用过期地方法new Date(String s),但是格式必须完整,不能缺少年份,此方法就是要做处理
	 * */
	private static Date format_en2(String regexStr,String pattern){
		regexStr=regexStr.trim();
//		String result=null;
//		String[] strArray=regexStr.split(" ");
//		int length=strArray.length;
//		if(strArray[length].indexOf(":")>0){//没有年份
//		   result=regexStr+" "+getCurrentYear();//添加当前年份
//		}
		return new Date(regexStr);
		//return parseDate(result,pattern);
	}
	
	private static Date format_num3(String regexStr,String pattern){
		return parseDate(regexStr,pattern);
	}
	
	private static Date format_metaPostDate(String dateStr)
	{
		dateStr = dateStr.trim();
		dateStr = dateStr.replaceAll("[年月/]", "-");
		dateStr = dateStr.replaceAll("日", " ");
		
		String yearStr = dateStr.substring(0, dateStr.indexOf('-'));
		if(yearStr.length() == 2)
		{
			dateStr = "20" + dateStr;
		}
		
		int colonNum =0;
		char[] charArray = dateStr.toCharArray();
		for(char c:charArray)
		{
			if(c== ':')
				colonNum++;
		}
		
		switch(colonNum)
		{
		case 0:
			dateStr+=" 09:30:00";
			break;
		case 1:
			dateStr+=":00";
			break;
		case 2:
			break;
		default:
			break;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * 目前对于Thu Jan 6 18:44:56 2005这种格式的日期,simpleDateFormat无法转换,只能
	 * 用过期地方法new Date(String s),但是格式必须完整,不能缺少年份,此方法就是要做处理
	 * */
	private static Date format_en3(String regexStr,String pattern){
		regexStr=regexStr.trim();
//		String result=null;
//		String[] strArray=regexStr.split(" ");
//		int length=strArray.length;
//		if(strArray[length].indexOf(":")>0){//没有年份
//		   result=regexStr+" "+getCurrentYear();//添加当前年份
//		}
		return new Date(regexStr);
		//return parseDate(result,pattern);
	}
	
	/**
	 * 把pattern_cn匹配到得日期字符串格式化为标准格式:2009-9-11 7:33,
	 * 没有年份的默认添加当前年份,没有时间的默认添加09:30
	 * regexStr格式为2009年9月11号 7时33分,
	 * */
	private static Date format_cn(String regexStr,String pattern){
//		System.out.println("regexStr:"+regexStr);
		regexStr=regexStr.trim();
		regexStr=regexStr.replaceAll("[年月]", "-");
		
		String digitPattern = "号\\s*|日\\s*";
		Pattern p = Pattern.compile(digitPattern);
		Matcher m = p.matcher(regexStr);
		
		regexStr = m.replaceAll(" ");
//		System.out.println("regexStr--"+regexStr);
		
		regexStr=regexStr.replaceAll("分", "");
		regexStr=regexStr.replaceAll("时", ":");
		
		String dateStr = null;//日期,如09-9-11或者9-11
		String timeStr = null;//时间,如
		String[] strs = null;
		String result = "";
		//判断是否有时间
		if(regexStr.indexOf(":")>0){//有时间
			strs = regexStr.split(" ");
			
			if((strs != null) && (strs.length ==1)){
				dateStr = strs[0];
			}else if((strs != null) && (strs.length > 1)){
				dateStr = strs[0];
				timeStr = strs[1];
			}
		}else{//没有时间--!
			dateStr=regexStr;	
			timeStr="09:30";//添加默认时间--!
			//str=dateStr+" "+timeStr;
		}
		//判断是否有年份
		if((dateStr != null) && (timeStr != null)){
			if(dateStr.split("-").length<=2){//没年份
				dateStr=getCurrentYear()+"-"+dateStr;
			}
			result = dateStr + " " + timeStr;
			
		}
		
		return parseDate(result,pattern);
	}
	
	/**
	 * @param str已经处理过的标准日期字符串
	 * @param pattern与str对应的正则表达式
	 * */
	public static Date parseDate(String str,String pattern){
		SimpleDateFormat sdf=new SimpleDateFormat(patternMap.get(pattern));
		Date resultDate=null;
		if(!"".equals(str)){
			try {
				resultDate=sdf.parse(str);
			} catch (ParseException e){
				e.printStackTrace();
			}
		}
		return resultDate;
	}
	
	/**
	 * @param text需要从中提取时间的正文
	 * @return 返回第一个匹配到的日期
	 * */
	public static Date extractDate(String text)
	{
		String date2=RegexUtil.getMatchGroupRegex(text,pattern_newsPostDate);
		Date date = new Date();
	    if(date2!=null&&!"".equals(date2)){
	    	date=format_cn(date2,pattern_newsPostDate);
	    }
		return date;
	}
	
	/**
	 * 提取新闻时间，如果没有则返回null
	 */
	public static Date getNewsPostDate(String text)
	{
		String dateStr = RegexUtil.getMatcherRegex(text, pattern_newsPostDate);
		return dateStr == null? null:format_metaPostDate(dateStr);
	}
	
	/**
	 * @param text需要从中提取时间的正文
	 * @return 返回全部匹配到得日期数组
	 * */
	public static Date[] extractDates(String text){
		if(text==null){
			return new Date[]{new Date()};
		}
		//对每一种日期格式进行匹配, 把匹配到得结果放在各自的数组里面
	    List<String> date1 = RegexUtil.getAllMatcherListRegex(text,pattern_num1);
	    List<String> date2 = RegexUtil.getAllMatcherListRegex(text,pattern_cn);
	    List<String> date3 = RegexUtil.getAllMatcherListRegex(text,pattern_num2);
	    List<String> date4 = RegexUtil.getAllMatcherListRegex(text,pattern_en1);
	    List<String> date5 = RegexUtil.getAllMatcherListRegex(text,pattern_en2);
	    List<String> date6 = RegexUtil.getAllMatcherListRegex(text,pattern_num3);
	    List<String> date7 = RegexUtil.getAllMatcherListRegex(text, pattern_metaPostDate);
	    Date[] dateArray=new Date[date1.size()+date2.size()+
	                              date3.size()+date4.size()+
	                              date5.size()+date6.size()+
	                              date7.size()];
	    int count=0;
	    for(String d : date1){
	    	dateArray[count++]=format_num1(d,pattern_num1);
	    }
	    for(String d : date2){
	    	dateArray[count++]=format_cn(d,pattern_cn);
	    }
	    for(String d : date3){
	    	dateArray[count++]=format_num2(d,pattern_num2);
	    	
	    }
	    for(String d : date4){
	    	dateArray[count++]=format_en1(d,pattern_en1);
	    	
	    }
	    for(String d : date5){
	    	dateArray[count++]=format_en2(d,pattern_en2);
	    }
	   
	    for(String d : date6){
	    	dateArray[count++]=format_num3(d,pattern_num3);
	    }
	    
	    for(String d:date7){
	    	dateArray[count++] = format_metaPostDate(d);
	    }
	    
	    
		return sortDate(dateArray);
	}
	
	/**
	 * 对日期数组排序,以当前时间为准,从近到远
	 * 考虑到数组不会太大,采用冒泡排序
	 * */
	public static Date[] sortDate(Date[] dateArray){
		Date first=null;
		Date temp=null;
		int length=dateArray.length;
		for(int j=0;j<length;j++){
		  for(int i=j+1;i<length;i++){
			if((dateArray[i] != null) && (dateArray[j] != null) && (dateArray[i].before(dateArray[j]))){
				temp=dateArray[j];
				dateArray[j]=dateArray[i];
				dateArray[i]=temp;
			}
		  }
		}
		return dateArray;
	}
	

	
	/**
	 * 获得新闻,论坛帖子等的发布时间
	 * 所得时间不得早于当前时间
	 * */
	public static Date getPostDate(String text){
		Date[] matchDate = extractDates(text);
		//matchDate=sortDate(matchDate);
		Date postDate=null;
		if(matchDate.length==0){
			return null;
		}else{
			for(int i=0;i<matchDate.length;i++){
				if(matchDate[i]!=null){
					if(matchDate[i].before(new Date())){
						if(postDate == null){
							postDate = matchDate[i];
							break;
						}else if(matchDate[i].before(postDate)){
							postDate = matchDate[i];
							break;
						}
					}
				}else{
					continue;
				}
			}
		}
		return postDate;
	}
	
	public static Date getBBSPostDate(String text, String dateRegex)
	{
		
		List<String> dateStrList = null;
		String regex = dateRegex == null ? 
				pattern_BBSPostDate:dateRegex;
		dateStrList = RegexUtil.getAllMatcherListRegexGroup1(text, regex ,0);
		
		if(dateStrList == null || dateStrList.size() == 0)
		{
			return null;
		}
		
		Date now = new Date();
		Date date;
		for(String dateStr: dateStrList)
		{
			date = format_metaPostDate(dateStr); 
			if(date.before(now))
			{
				return date;
			}
		}
		
		return null;
	}
	
	public static Date getMetaPostDate(String text)
	{
	    
		List<String> dateStrList = 
			RegexUtil.getAllMatcherListRegexGroup1(text, pattern_metaPostDate,0);
		
		Date now = new Date();
		if(dateStrList == null || dateStrList.size() == 0)
		{
			return now;
		}
		
		Date date;
		for(String dateStr: dateStrList)
		{
			date = format_metaPostDate(dateStr); 
			if(date.before(now))
			{
				return date;
			}
		}
		
		return now;
	} 

	
	public static String getCurrentYear(){
		Calendar c1=Calendar.getInstance();
		c1.setTime(new Date());
		Integer year=c1.get(Calendar.YEAR);
		return year.toString();
	}
	
	public static String getCurrentMonth(){
		Calendar c1=Calendar.getInstance();
		c1.setTime(new Date());
		Integer month=c1.get(Calendar.MONTH);
		return month.toString();
	}
	
	public static String getCurrentDay(){
		Calendar c1=Calendar.getInstance();
		c1.setTime(new Date());
		Integer day=c1.get(Calendar.DAY_OF_MONTH);
		return day.toString();
	}
	
	public static String getCurrentHour(){
		Calendar c1=Calendar.getInstance();
		c1.setTime(new Date());
		Integer hour=c1.get(Calendar.HOUR_OF_DAY);
		return hour.toString();
	}
	
	public static String getCurrentMinute(){
		Calendar c1=Calendar.getInstance();
		c1.setTime(new Date());
		Integer minute=c1.get(Calendar.MINUTE);
		return minute.toString();
	}
	
//	public static void main(String args[]){
//		Fetcher f=new Fetcher();
//		String html=f.getPage("http://topic.csdn.net/u/20070914/17/004ec7a6-24fa-4db0-b7d8-5f92baa17a60.html",false);
//		String text="http://www.sina.com.cn  2009年07月10日 13:23 2008年09月10日 13:23  新闻晚报 09-07-09 12:33,Thu 6 Jan 2005 18:44:56点击的路口 09-7-11 10:29jkjlkj 7-11 10:40";
//		String plainText=HTMLParserExtractor.getPlainText(html,"gb2312");
//		Date[] dates=extractDate(plainText);
//		
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		for(int i=0;i<dates.length;i++){
//			String tempDate=sdf.format(dates[i]);
//			System.out.println(tempDate);
//		}
////		Date postDate=getPostDate(text);
////		System.out.println(postDate.toString());
//		
//	}
	 public static void main(String[] args)  
	 {  
	     String date = "";     
	     SimpleDateFormat  sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
	     try {
			System.out.println(sdf.parse("2010-5-12 14:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Integer> intList = new ArrayList<Integer>();
		intList.add(2);
		intList.add(5);
		intList.add(1);
		Collections.sort(intList);
		System.out.println(intList);	 
		}  
	   
	 public static boolean isDateBefore(String date2)  
	 {  
	     try{  
		     Date date1 = new Date();  
		     DateFormat df = DateFormat.getDateTimeInstance();  
		     return date1.before(df.parse(date2));   
	     }catch(ParseException e){  
	    	 System.out.print(e.getMessage());  
	     return false;  
	     }  
	 }  
	 
	 public static boolean isDateBefore(String dateBegin,String dateEnd){  
		 if((dateBegin != null) && (!"".equals(dateBegin)) 
				 && (dateEnd != null) && (!"".equals(dateEnd))){
			 dateBegin = (dateBegin.indexOf(".")>-1)?dateBegin.substring(0,dateBegin.indexOf(".")):dateBegin;
			 dateEnd = (dateEnd.indexOf(".")>-1)?dateEnd.substring(0,dateEnd.indexOf(".")):dateEnd;
			 DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date d1 = null,d2 = null;
			try {
				d1 = df.parse(dateBegin);
				d2 = df.parse(dateEnd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
//			System.out.println("dateBegin: " + dateBegin);  
//			System.out.println("dateEnd: " + dateEnd);
			return d2.after(d1); 
		 }
		 return false;
	 }
	   
	 public static boolean isDateAfter(String date2){  
	     try{  
	         Date date1 = new Date();  
	         DateFormat df = DateFormat.getDateTimeInstance();  
	         System.out.println(date1.after(df.parse(date2)));  
	         return date1.after(df.parse(date2));   
	     }catch(ParseException e){  
	         System.out.print(e.getMessage());  
	         return false;  
	     }  
	 } 
	
	
	 public static Date getDetailDate(String dateString){
		
		 if(dateString.length()  == 19){
			 try {
				return detailFormat.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		 }
		 return null;
	 }
	
	
}

  
