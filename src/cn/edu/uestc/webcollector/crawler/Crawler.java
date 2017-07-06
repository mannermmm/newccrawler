package cn.edu.uestc.webcollector.crawler;

import cn.edu.uestc.webcollector.crawldb.DBManager;
import cn.edu.uestc.webcollector.crawldb.Generator;
import cn.edu.uestc.webcollector.fetcher.Fetcher;
import cn.edu.uestc.webcollector.fetcher.Visitor;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.model.CrawlDatums;
import cn.edu.uestc.webcollector.model.Links;
import cn.edu.uestc.webcollector.net.Requester;
import cn.edu.uestc.webcollector.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 
 */
public abstract class Crawler {

    public static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    protected int status;
    public final static int RUNNING = 1;
    public final static int STOPED = 2;
    protected boolean resumable = false;
    protected int threads = 50;

    protected int topN = -1;
    protected int retry = 3;
    protected long retryInterval = 0;
    protected long visitInterval = 0;

    protected CrawlDatums seeds = new CrawlDatums();
    protected CrawlDatums forcedSeeds = new CrawlDatums();
    protected Fetcher fetcher;
    protected int maxRetry = -1;

    protected Requester requester;
    protected Visitor visitor;
    protected DBManager dbManager;
    protected Generator generator;

    protected void inject() throws Exception {
        dbManager.inject(seeds);
    }

    public void injectForcedSeeds() throws Exception {
        dbManager.inject(forcedSeeds);
    }

    public void start(int depth) throws Exception {

        boolean needInject = true;

        if (resumable && dbManager.isDBExists()) {
            needInject = false;
        }

        if (!resumable) {
            if (dbManager.isDBExists()) {
                dbManager.clear();
            }

            if (seeds.isEmpty() && forcedSeeds.isEmpty()) {
                LOG.info("error:Please add at least one seed");
                return;
            }

        }
        dbManager.open();

        if (needInject) {
            inject();
        }

        if (!forcedSeeds.isEmpty()) {
            injectForcedSeeds();
        }

        status = RUNNING;
        for (int i = 0; i < depth; i++) {
            if (status == STOPED) {
                break;
            }
            LOG.info("start depth " + (i + 1));
            long startTime = System.currentTimeMillis();

            if (maxRetry >= 0) {
                generator.setMaxRetry(maxRetry);
            } else {
                generator.setMaxRetry(Config.MAX_RETRY);
            }
            //调试用
            if (i == 4){
            	i = 5;
            }
            generator.setTopN(topN);
            fetcher = new Fetcher();
            fetcher.setRetryInterval(retryInterval);
            fetcher.setVisitInterval(visitInterval);
            fetcher.setDBManager(dbManager);
            fetcher.setRequester(requester);
            fetcher.setVisitor(visitor);
            fetcher.setRetry(retry);
            fetcher.setThreads(threads);
            fetcher.fetchAll(generator);
            long endTime = System.currentTimeMillis();
            long costTime = (endTime - startTime) / 1000;
            int totalGenerate = generator.getTotalGenerate();

            LOG.info("depth " + (i + 1) + " finish: \n\tTOTAL urls:\t" + totalGenerate + "\n\tTOTAL time:\t" + costTime + " seconds");
            if (totalGenerate == 0) {
                break;
            }

        }
        dbManager.close();
    }

    public void stop() {
        status = STOPED;
        fetcher.stop();
    }

    public void addSeed(CrawlDatum datum, boolean force) {
        if (force) {
            forcedSeeds.add(datum);
        } else {
            seeds.add(datum);
        }
    }

    public void addSeed(CrawlDatum datum) {
        addSeed(datum, false);
    }

    public void addSeed(CrawlDatums datums, boolean force) {
        for (CrawlDatum datum : datums) {
            addSeed(datum, force);
        }
    }

    public void addSeed(CrawlDatums datums) {
        addSeed(datums, false);
    }

    public void addSeed(Links links, boolean force) {
        for (String url : links) {
            addSeed(url, force);
        }
    }

    public void addSeed(Links links) {
        addSeed(links, false);
    }

    public void addSeed(String url, boolean force) {
        CrawlDatum datum = new CrawlDatum(url);
        addSeed(datum, force);
    }

    public void addSeed(String url) {
        addSeed(url, false);
    }

    public boolean isResumable() {
        return resumable;
    }

    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public long getVisitInterval() {
        return visitInterval;
    }

    public void setVisitInterval(long visitInterval) {
        this.visitInterval = visitInterval;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

}
