package cn.edu.uestc.webcollector.plugin.ram;

import cn.edu.uestc.webcollector.crawler.BasicCrawler;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.model.CrawlDatums;
import cn.edu.uestc.webcollector.model.Links;
import cn.edu.uestc.webcollector.model.Page;

/**
 * 基于内存的Crawler插件，适合一次性爬取，并不具有断点爬取功能
 * 长期任务请使用BreadthCrawler
 * 
 * @author 
 */
public abstract class RamCrawler extends BasicCrawler {
    
    public RamCrawler(){
        this(true);
    }

    public RamCrawler(boolean autoParse) {
        super(autoParse);
        RamDB ramDB = new RamDB();
        this.dbManager = new RamDBManager(ramDB);
        this.generator = new RamGenerator(ramDB);
    }
    
    public void start() throws Exception{
        start(Integer.MAX_VALUE);
    }

    public static void main(String[] args) throws Exception {
        RamCrawler crawler = new RamCrawler(true) {
            public void visit(Page page, CrawlDatums next) {

                if (page.getMetaData("type") != null) {
                    Links links = page.getLinks("div.infos.media-body a");
                    CrawlDatums datums=new CrawlDatums(links)
                            .putMetaData("refer", page.getUrl());
                    next.add(datums);
                } else {
                    System.out.println(page.getUrl()+" refer:"+page.getMetaData("refer"));
                    System.out.println(page.getDoc().title());
                }
            }
        };
        for (int i = 0; i <= 2; i++) {
            String url = "https://ruby-china.org/topics?page=" + (i + 1);
            CrawlDatum seed = new CrawlDatum(url)
                    .putMetaData("type", "nav");
            crawler.addSeed(seed);
        }
        crawler.setRetryInterval(3000);
        crawler.start(3);

    }

}
