package cn.uestc.algorithm;

import java.util.Date;



public class dateorSource {
	static{
		System.loadLibrary("dorsource");//（无错版本是19）
	}
	public String source;
	public Date date;
	public native  String[] dateAndSource(String content, String url, int i);
}
