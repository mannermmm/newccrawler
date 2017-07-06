package cn.edu.uestc.webcollector.model;

import cn.edu.uestc.webcollector.net.HttpResponse;
import cn.edu.uestc.webcollector.util.CharsetDetector;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page是爬取过程中，内存中保存网页爬取信息的一个容器，Page只在内存中存 放，用于保存一些网页信息，方便用户进行自定义网页解析之类的操作。
 *
 * @author 
 */
public class Page {

	public static final Logger LOG = LoggerFactory.getLogger(Page.class);

	public static final int STATUS_FETCH_UNKNOWN = 0;
	public static final int STATUS_FETCH_FAILED = 1;
	public static final int STATUS_FETCH_SUCCESS = 2;

	private CrawlDatum crawlDatum = null;

	private int status = STATUS_FETCH_UNKNOWN;
	private HttpResponse response = null;
	private Exception exception = null;

	private String html = null;
	private Document doc = null;
	private int retry = 0;

	/**
	 * 判断当前Page的URL是否和输入正则匹配
	 * 
	 * @param urlRegex
	 * @return
	 */
	public boolean matchUrl(String urlRegexs) {
		boolean flag = false;
		String[] urlRegexstr = urlRegexs.split("\\|");
		for (String urlRegex : urlRegexstr) {

			flag = Pattern.matches(urlRegex, getUrl());
			if (flag)
				break;
		}
		return flag;
	}

	/**
	 * 获取网页中满足指定css选择器的所有元素的指定属性的集合
	 * 例如通过getAttrs("img[src]","abs:src")可获取网页中所有图片的链接
	 * 
	 * @param cssSelector
	 * @param attrName
	 * @return
	 */
	public ArrayList<String> getAttrs(String cssSelector, String attrName) {
		ArrayList<String> result = new ArrayList<String>();
		Elements eles = select(cssSelector);
		for (Element ele : eles) {
			if (ele.hasAttr(attrName)) {
				result.add(ele.attr(attrName));
			}
		}
		return result;
	}

	/**
	 * 获取满足选择器的元素中的链接 选择器cssSelector必须定位到具体的超链接 例如我们想抽取id为content的div中的所有超链接，这里
	 * 就要将cssSelector定义为div[id=content] a
	 *
	 * @param cssSelector
	 * @return
	 */
	public Links getLinks(String cssSelector) {
		Links links = new Links().addBySelector(doc, cssSelector);
		return links;
	}

	public Elements select(String cssSelector) {
		return this.getDoc().select(cssSelector);
	}

	public Element select(String cssSelector, int index) {
		Elements eles = select(cssSelector);
		int realIndex = index;
		if (index < 0) {
			realIndex = eles.size() + index;
		}
		return eles.get(realIndex);
	}

	public String regex(String regex, int group, String defaultResult) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(getHtml());
		if (matcher.find()) {
			return matcher.group(group);
		} else {
			return defaultResult;
		}
	}

	public String regex(String regex, int group) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(getHtml());
		matcher.find();
		return matcher.group(group);
	}

	public String regex(String regex, String defaultResult) {
		return regex(regex, 0, defaultResult);
	}

	public String regex(String regex) {
		return regex(regex, 0);
	}

	public static Page createSuccessPage(CrawlDatum crawlDatum, int retry,
			HttpResponse response) {
		return new Page(crawlDatum, STATUS_FETCH_SUCCESS, retry, response);
	}

	public static Page createFailedPage(CrawlDatum crawlDatum, int retry) {
		return new Page(crawlDatum, STATUS_FETCH_FAILED, retry, null);
	}

	public static Page createFailedPage(CrawlDatum crawlDatum, int retry,
			Exception ex) {
		return new Page(crawlDatum, STATUS_FETCH_FAILED, retry, null, ex);
	}

	Page(CrawlDatum crawlDatum, int status, int retry, HttpResponse response) {
		this.crawlDatum = crawlDatum;
		this.status = status;
		this.retry = retry;
		this.response = response;
	}

	Page(CrawlDatum crawlDatum, int status, int retry, HttpResponse response,
			Exception exception) {
		this.crawlDatum = crawlDatum;
		this.status = status;
		this.retry = retry;
		this.response = response;
		this.exception = exception;
	}

	/**
	 * 返回网页/文件的内容
	 *
	 * @return 网页/文件的内容
	 */
	public byte[] getContent() {
		if (response == null) {
			return null;
		}
		return response.getContent();
	}

	/**
	 * 返回网页的url
	 *
	 * @return 网页的url
	 */
	public String getUrl() {
		return crawlDatum.getUrl();
	}

	/**
	 * 返回网页的源码字符串
	 *
	 * @return 网页的源码字符串
	 */
	public String getHtml() {
		if (html != null) {
			return html;
		}
		if (response.getHtml() != null) {
			html = response.getHtml();
			return html;
		}
		if (getContent() == null) {
			return null;
		}
		String charset = CharsetDetector.guessEncoding(getContent());
		try {
			this.html = new String(getContent(), charset);
			return html;
		} catch (UnsupportedEncodingException ex) {
			LOG.info("Exception", ex);
			return null;
		}
	}

	/**
	 * 设置网页的源码字符串
	 *
	 * @param html
	 *            网页的源码字符串
	 */
	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * 返回网页解析后的DOM树(Jsoup的Document对象)
	 *
	 * @return 网页解析后的DOM树
	 */
	public Document getDoc() {
		if (doc != null) {
			return doc;
		}
		try {

			this.doc = Jsoup.parse(getHtml(), getUrl());
			return doc;
		} catch (Exception ex) {
			LOG.info("Exception", ex);
			return null;
		}

	}

	/**
	 * 设置网页解析后的DOM树(Jsoup的Document对象)
	 *
	 * @param doc
	 *            网页解析后的DOM树
	 */
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public CrawlDatum getCrawlDatum() {
		return crawlDatum;
	}

	public void setCrawlDatum(CrawlDatum crawlDatum) {
		this.crawlDatum = crawlDatum;
	}

	public HashMap<String, String> getMetaData() {
		return crawlDatum.getMetaData();
	}

	public void setMetaData(HashMap<String, String> metaData) {
		this.crawlDatum.setMetaData(metaData);
	}

	public void putMetaData(String key, String value) {
		this.crawlDatum.putMetaData(key, value);
	}

	public String getMetaData(String key) {
		return this.crawlDatum.getMetaData(key);
	}

}
