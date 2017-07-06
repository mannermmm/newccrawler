package cn.edu.uestc.webcollector.crawldb;

import cn.edu.uestc.webcollector.model.CrawlDatum;

/**
 *
 * @author 
 */
public interface Injector {
     public void inject(CrawlDatum datum) throws Exception;
}
