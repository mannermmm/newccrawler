package cn.edu.uestc.webcollector.net;

import cn.edu.uestc.webcollector.model.CrawlDatum;

/**
 *
 * @author 
 */
public interface Requester {
     public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception;
}
