package cn.edu.uestc.webcollector.fetcher;

import cn.edu.uestc.webcollector.model.CrawlDatums;
import cn.edu.uestc.webcollector.model.Page;

/**
 *
 * @author
 */
public interface Visitor {

    public abstract void visit(Page page, CrawlDatums next);

    public abstract void afterVisit(Page page, CrawlDatums next);

    public abstract void fail(Page page, CrawlDatums next);

}
