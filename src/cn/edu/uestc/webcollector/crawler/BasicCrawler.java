package cn.edu.uestc.webcollector.crawler;

import java.io.File;
import java.net.Proxy;

import cn.edu.uestc.webcollector.fetcher.Visitor;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.model.CrawlDatums;
import cn.edu.uestc.webcollector.model.Links;
import cn.edu.uestc.webcollector.model.Page;
import cn.edu.uestc.webcollector.net.HttpRequest;
import cn.edu.uestc.webcollector.net.HttpResponse;
import cn.edu.uestc.webcollector.net.Proxys;
import cn.edu.uestc.webcollector.net.Requester;
import cn.edu.uestc.webcollector.util.RegexRule;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 
 */
public abstract class BasicCrawler extends Crawler implements Visitor,
		Requester {

	public static final Logger LOG = LoggerFactory
			.getLogger(BasicCrawler.class);

	/**
	 * URL正则约束
	 */
	protected RegexRule regexRule = new RegexRule();

	/**
	 * 是否自动抽取符合正则的链接并加入后续任务
	 */
	protected boolean autoParse = true;
	protected Proxys proxys;

	public BasicCrawler(boolean autoParse) {
		this.visitor = this;
		this.requester = this;
		this.autoParse = autoParse;
		// 设置代理ip
		proxys = new Proxys();
		File file = new File("E:\\爬虫\\data\\uestc\\iplist.txt");
		if (file.isFile() && file.exists()) {
			try {
				proxys.addAllFromFile(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("系统文件打开错误");
		}
	}

	public void afterVisit(Page page, CrawlDatums next) {
		if (autoParse && !regexRule.isEmpty()) {

			String conteType = page.getResponse().getContentType();
			if (conteType != null && conteType.contains("text/html")) {
				Document doc = page.getDoc();
				if (doc != null) {
					Links links = new Links().addByRegex(doc, regexRule);
					if (links.isEmpty()){
						System.out.println("没有下一个链接了");
					}
					next.add(links);
				}
			}
		}
	}

	public void fail(Page page, CrawlDatums next) {
	}

	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		Proxy proxy = proxys.nextRandom();
		HttpRequest request=new HttpRequest(crawlDatum, proxy);
		//HttpRequest request = new HttpRequest(crawlDatum);
		return request.getResponse();
	}

	/**
	 * 添加URL正则约束
	 *
	 * @param urlRegex
	 */
	public void addRegex(String urlRegex) {
		regexRule.addRule(urlRegex);
	}

	/**
	 *
	 * @return 返回是否自动抽取符合正则的链接并加入后续任务
	 */
	public boolean isAutoParse() {
		return autoParse;
	}

	/**
	 * 设置是否自动抽取符合正则的链接并加入后续任务
	 *
	 * @param autoParse
	 */
	public void setAutoParse(boolean autoParse) {
		this.autoParse = autoParse;
	}

	/**
	 *
	 * @return
	 */
	public RegexRule getRegexRule() {
		return regexRule;
	}

	/**
	 *
	 * @param regexRule
	 */
	public void setRegexRule(RegexRule regexRule) {
		this.regexRule = regexRule;
	}

}
