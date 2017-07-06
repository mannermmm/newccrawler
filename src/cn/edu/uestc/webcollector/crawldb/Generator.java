package cn.edu.uestc.webcollector.crawldb;

import cn.edu.uestc.webcollector.model.CrawlDatum;

/**
 * 抓取任务生成器
 *
 * @author 
 */
public interface Generator {

    public CrawlDatum next();
    
    public void open() throws Exception;

    public void setTopN(int topN);

    public void setMaxRetry(int maxRetry);

    public int getTotalGenerate();

    public void close() throws Exception;

}
