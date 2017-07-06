package cn.uestc.test.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;










import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.uestc.webcollector.model.CrawlDatums;
import cn.edu.uestc.webcollector.model.Page;
import cn.edu.uestc.webcollector.plugin.ram.RamCrawler;
import cn.engine.utils.DateUtil;
import cn.engine.utils.RegexUtil;
import cn.engine.utils.UUIDUtil;
import cn.uestc.algorithm.dateorSource;
import cn.uestc.algorithm.newsparse;

public class Tutorial2Crawler extends RamCrawler {
	private static Connection conn = null;
	private static boolean bflag = false;// 数据库是否连接成功
	private static String lastTime = "";// 上一次爬取的时间最近的新闻（每次爬去都必须大于这个时间）
	private static String currTime = "";// 现在爬去的最近新闻
	private static newsparse p = null;
	private static dateorSource date1 = null;

	private String parseSource(String sourceAndDate) {
		String sourceRegularExpr = "(来\\s*源|出\\s*处)\\s*[:：\\s]\\s*[^\\s\\u3000]+";
		String source = RegexUtil.getMatcherRegex(sourceAndDate,
				sourceRegularExpr);
		// log.info("\n==parseSource==source:"+source);
		if (source != null) {
			String[] tokens = source.split("[:：\\s]");
			if (tokens.length == 2) {

				return tokens[1];
			} else if (tokens.length == 3) {
				if (tokens[2].indexOf("报") != -1
						|| tokens[2].indexOf("网") != -1
						|| tokens[2].indexOf("社") != -1) {
					return tokens[2];
				} else {
					return parseSourcewithoutKey(tokens[1]);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private String parseSourcewithoutKey(String sourceAndDate) {

		String sourceRegularExpr = "[\u4e00-\u9fa5][\u4e00-\u9fa5]*[报网]";
		String source = RegexUtil.getMatcherRegex(sourceAndDate,
				sourceRegularExpr);
		// log.info("\n==parseSourcewithoutKey==source:"+source);
		if (source != null) {
			return source;
		} else {
			return null;
		}
	}

	private String getSourceFromTitle(String title) {
		int index = title.indexOf("_");
		int indexend = title.indexOf("_", index + 1);
		String source = "";
		if (indexend != -1)
			source = title.substring(index + 1, indexend);
		else
			source = title.substring(index + 1);
		return source;
	}

	private static void storeCurrentTime(String currtime) {
		try {
			FileWriter write = new FileWriter("lastTime.cfg");
			write.write(currtime);
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private static String readLastTime() {
		String lastTime = "";
		File read = new File("lastTime.cfg");
		try {
			BufferedReader rd = new BufferedReader(new FileReader(read));
			String s = rd.readLine();
			while (null != s) {
				lastTime += s;
				s = rd.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastTime;
	}

	public static boolean connectMysql() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/total_fy_clear";
		String user = "root";
		String password = "111111";
		boolean flag = false;
		try {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = DriverManager.getConnection(url, user, password);
			if (!conn.isClosed()) {
				System.out.println("Connection Success!!!!!!!");
				flag = true;
			} else {
				System.out.println("Connection Failed!!!!!!!");
				flag = false;
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}

	private String changQuotationMarks(String handleStr) {
		// test
		// String sss = "\"Live\" or \"Paused\" or \'dddd'";
		String Postprocessing = handleStr.replaceAll("\"", "\"\"");
		Postprocessing = Postprocessing.replaceAll("\'", "\'\'");
		return Postprocessing;
	}

	public void storeDatetoDisk(Date date, String source, newsparse n, String u) {
		//时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(date);
		try {
			FileWriter fw = new FileWriter("E:\\爬虫\\data\\uestc\\scu.txt",true);
			fw.write("\n");
			fw.write(u);
			fw.write("\n");
			fw.write(n.title);
			fw.write("\n");
			fw.write(time);
			fw.write("\n");
			fw.write(n.webText);
			fw.write("\n");
			fw.write("~~~");
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void storeDate(Date date, String source, newsparse n, String u) {
		String iduu = UUIDUtil.getUUID();
		String iduu1 = UUIDUtil.getUUID();
		String title = changQuotationMarks(n.title);
		String summary = changQuotationMarks(n.webText);
		String url = u;
		String webName = changQuotationMarks(source);
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String crawlerTime = sdf.format(time);// 爬取时间
		String newsTime = sdf.format(date);// 新闻时间
		// 去重
		if (newsTime.compareTo(lastTime) < 0 && lastTime != "")
			return;
		// 储存最新新闻时间作为下次爬取所用
		if (newsTime.compareTo(currTime) > 0) {
			currTime = newsTime;
			storeCurrentTime(currTime);
		}
		try {
			Statement statement = conn.createStatement();
			String sql = "INSERT INTO total_fy_clear.original_data(id, title, summary, url, webName) VALUES(\""
					+ iduu
					+ "\",\""
					+ title
					+ "\",\""
					+ summary
					+ "\",\""
					+ url + "\",\"" + webName + "\")";
			String sql_info = "INSERT INTO total_fy_clear.sub_info(id, originalDataId, crawlerTime) VALUES(\""
					+ iduu1 + "\",\"" + iduu + "\",\"" + crawlerTime + "\")";
			statement.executeUpdate(sql);
			statement.executeUpdate(sql_info);
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeHtmlCode(String htmlcode, String url, String name) {
		try {
			FileWriter write = new FileWriter("./html/" + name);
			write.write(url);
			write.write("\n");
			write.write(htmlcode);
			write.close();
			FileWriter url1 = new FileWriter("./html/name.txt", true);
			url1.write(name);
			url1.write("\n");
			url1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	private dateorSource getDateAndSource(String[] info, newsparse n) {
		Date date = null;
		String source = null;
		Date currdate = new Date();
		for (int i = 0; i < info.length; i++) {
			if (date == null) {
				date = DateUtil.getNewsPostDate(info[i]);
				if (date != null) {
					// 判定时间必须早于当前时间,flag为false
					boolean flag = date.after(currdate);
					if (flag) {
						date = null;
						continue;
					}
					if (source == null)
						source = this.parseSource(info[i]);
					if (source == null && i != 0)
						source = this.parseSourcewithoutKey(info[i - 1]);
					if (source == null && i != info.length - 1)
						source = this.parseSourcewithoutKey(info[i]);
					if (source == null && i != 0)
						source = this.parseSource(info[i - 1]);
					if (source == null && i != info.length - 1)
						source = this.parseSource(info[i + 1]);
					if (source == null)
						source = this.parseSourcewithoutKey(info[i + 1]);
				}
			}
			if (source != null && date != null)
				break;
		}
		if (source == null) {
			for (int i = 0; i < info.length; i++) {
				source = this.parseSource(info[i]);
				if (source != null)
					break;
			}
		}
		if (source == null) {
			source = getSourceFromTitle(n.title);
		}
		// 如果时间没有，则为爬取时间
		
		if (null == date){ date = currdate; }
		 
		dateorSource s = new dateorSource();
		s.date = date;
		s.source = source;
		return s;
	}

	public void visit(Page page, CrawlDatums next) {
		// String[] urlRegexs = new String[]{"http://www.infzm.com/content/.*",
		// "https://www.washingtonpost.com/.*.html.*"};
		//String urlRegex = "http://www.news.uestc.edu.cn/.*ArticlePage.*";
		String urlRegex = "http://news.scu.edu.cn/news2012/.*.htm";
		 //String urlRegex = "http://www.js.chinanews.com/news/.*.html";
		if (page.matchUrl(urlRegex)) {
			String[] date_source = null;
			newsparse n = null;
			String html = null;
			String url = null;
			try {
				Document doc = Jsoup.parse(page.getHtml(), page.getUrl());
				html = doc.html();
				url = page.getUrl();
				// String name = UUIDUtil.getUUID() + ".html";
				// storeHtmlCode(html, url, name);// 调试用的
				if (html != null && url != null) {
					n = p.paserNews(html, url, 3);
				}
				if (n.webText != "") {
					date_source = date1.dateAndSource(html, url, 3);
				}
				//获取特殊标题
				Elements eles = doc.getElementsByClass("Degas_news_title");
				for (Element ele:eles){
					String title = ele.text();
					n.title = title;
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			if (date_source.length == 0) {
				return;
			}
			dateorSource ds = getDateAndSource(date_source, n);
			//存到数据库中
			/*if (bflag) {
				if (ds.date == null)
					return;
				storeDate(ds.date, ds.source, n, url);
			} else {
				bflag = connectMysql();
				if (bflag) {
					if (ds.date == null)
						return;
					storeDate(ds.date, ds.source, n, url);
				}
			}*/
			//存入磁盘中
			
			storeDatetoDisk(ds.date, ds.source, n, url);
		}
		return;
	}

	public static void main(String[] args) throws Exception {
		lastTime = readLastTime();
		currTime = lastTime;
		bflag = connectMysql();
		p = new newsparse();
		date1 = new dateorSource();
		Tutorial2Crawler crawler = new Tutorial2Crawler();
		//crawler.addSeed("http://www.news.uestc.edu.cn/");
		crawler.addSeed("http://news.scu.edu.cn/");
		 //crawler.addSeed("http://www.js.chinanews.com/");
		// crawler.addSeed("http://china.huanqiu.com/article/2015-09/7412635.html");
		// crawler.addSeed("http://education.news.cn/2008-10/22/content_10231017.htm");
		// crawler.addSeed("http://www.infzm.com/");
		// crawler.addSeed("http://www.telegraph.co.uk/news/2016/04/26/labour-mp-backed-calls-to-relocate-israel-to-america/");
		// crawler.addSeed("http://www.washingtonpost.com/");
		// crawler.addRegex("http://www.js.chinanews.com/news/.*.html");
		// crawler.addRegex("http://china.huanqiu.com/article/.*.html");
		//crawler.addRegex("http://www.banyuetan.org/chcontent/.*.html");
		// crawler.addRegex("http://www.infzm.com/content/.*");
		// crawler.addRegex("http://www.telegraph.co.uk/news/.*");
		// crawler.addRegex("https://www.washingtonpost.com/.*.html.*");
		// crawler.addRegex("http://www.news.uestc.edu.cn/.*ArticlePage.*");
		 crawler.addRegex("http://news.scu.edu.cn/news2012/.*.htm");
		/* ��������ÿ���߳�visit�ļ���������Ǻ��� */
		// crawler.setVisitInterval(1000);
		/* ��������http�������Եļ���������Ǻ��� */
		// crawler.setRetryInterval(1000);
		crawler.setThreads(100);
		crawler.start(50);
		conn.close();
	}

}
