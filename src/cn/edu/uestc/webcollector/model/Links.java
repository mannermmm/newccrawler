package cn.edu.uestc.webcollector.model;

import cn.edu.uestc.webcollector.util.RegexRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 用于存储多个URL的数据结构，继承于ArrayList
 *
 * @author 
 */
public class Links implements Iterable<String> {

    protected ArrayList<String> dataList = new ArrayList<String>();

    public Links() {

    }

    public Links(Links links) {
        add(links);
    }

    public Links(Collection<String> urls) {
        add(urls);
    }

    public Links add(String url) {
        dataList.add(url);
        return this;
    }

    public Links add(Links links) {
        for (String url : links) {
            dataList.add(url);
        }
        return this;
    }

    public Links add(Collection<String> urls) {
        dataList.addAll(urls);
        return this;
    }

    public Iterator<String> iterator() {
        return dataList.iterator();
    }

    public Links filterByRegex(RegexRule regexRule) {
        for (int i = 0; i < size(); i++) {
            String url = get(i);
            if (!regexRule.satisfy(url)) {
                remove(i);
                i--;
            }
        }
        return this;
    }

    public Links filterByRegex(String regex) {
        RegexRule regexRule = new RegexRule();
        regexRule.addRule(regex);
        return filterByRegex(regexRule);
    }

    public Links addAllFromDocument(Document doc) {
        addBySelector(doc, "a");
        return this;
    }

    /**
     * 添加doc中，满足选择器的元素中的链接 选择器cssSelector必须定位到具体的超链接
     * 例如我们想抽取id为content的div中的所有超链接，这里 就要将cssSelector定义为div[id=content] a
     *
     * @param doc
     * @param cssSelector
     */
    public Links addBySelector(Document doc, String cssSelector) {
        Elements as = doc.select(cssSelector);
        for (Element a : as) {
            if (a.hasAttr("href")) {
                String href = a.attr("abs:href");
                this.add(href);
            }
        }
        return this;
    }

    public Links addByRegex(Document doc, String rule) {
        RegexRule regexRule = new RegexRule();
        regexRule.addRule(rule);
        Elements as = doc.select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            if (regexRule.satisfy(href)) {
                this.add(href);
            }
        }
        return this;
    }

    public Links addByRegex(Document doc, RegexRule regexRule) {
        Elements as = doc.select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
            if (regexRule.satisfy(href)) {
                this.add(href);
            }
        }
        return this;
    }

    public String get(int index) {
        return dataList.get(index);
    }

    public int size() {
        return dataList.size();
    }

    public String remove(int index) {
        return dataList.remove(index);
    }

    public boolean remove(String url) {
        return dataList.remove(url);
    }

    public void clear() {
        dataList.clear();
    }

    public boolean isEmpty() {

        return dataList.isEmpty();
    }

    public int indexOf(String url) {
        return dataList.indexOf(url);
    }

    @Override
    public String toString() {
        return dataList.toString();
    }
    
    
}
