package cn.engine.utils;

import java.util.LinkedList;
import java.util.List;

public class StringUtil 
{
	public static String BLANK_REGEX = "\\s|\\xa0| ";
	public static String removeBlank(String text)
	{
		if(text == null)
			return null;
		
		return text.replaceAll(BLANK_REGEX, "");
		
	}
	
	public static List<Integer> getPositions(String content, String token)
	{
		List<Integer> positions = new LinkedList<Integer>();
		
		if(content == null || content.length() == 0 ||
				token == null || token.length() ==0)
			return positions;
		
		int startPosition = 0;
		int tokenLen = token.length();
		while (true) {
			startPosition = content.indexOf(token, startPosition);
			if (startPosition > -1) {
				positions.add(new Integer(startPosition));
				startPosition += tokenLen;

			} else {
				break;// 在str中找不到word就结束查找
			}
		}
		return positions;
	}
	
	public static List<Integer> getPositions(String content, Character c)
	{
		List<Integer> positions = new LinkedList<Integer>();
		
		if(content == null || content.length() == 0 ||
			 c == null )
			return positions;
		
		int startPosition = 0;
		while(true)
		{
			startPosition = content.indexOf(c, startPosition);
			if (startPosition > -1) {
				positions.add(new Integer(startPosition));
				startPosition ++;
			} else {
				break;// 在str中找不到word就结束查找
			}
		}
		
		return positions;
		
	}

}
