package cn.uestc.algorithm;


public class newsparse {
	static{
		System.loadLibrary("newsparse");//(无错版本在bin中，5月23日的)
	}
	public String webText;
	public String title;
	public String fatherLableName;
	public String fatherLableClass;
	public String fatherLableId;
	public String LableName;
	public String LableClass;
	public boolean bflag;
	public native newsparse paserNews(String content, String url, int i);
}
