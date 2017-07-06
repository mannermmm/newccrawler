package cn.edu.uestc.webcollector.plugin.ram;

import cn.edu.uestc.webcollector.model.CrawlDatum;

import java.util.HashMap;

/**
 *
 * @author 
 */
public class RamDB {

    protected HashMap<String, CrawlDatum> crawlDB = new HashMap<String, CrawlDatum>();
    protected HashMap<String, CrawlDatum> fetchDB = new HashMap<String, CrawlDatum>();
    protected HashMap<String, CrawlDatum> linkDB = new HashMap<String, CrawlDatum>();
    protected HashMap<String, String> redirectDB = new HashMap<String, String>();
}
