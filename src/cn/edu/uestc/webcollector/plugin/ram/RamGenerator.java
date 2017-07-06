package cn.edu.uestc.webcollector.plugin.ram;

import cn.edu.uestc.webcollector.crawldb.Generator;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.util.Config;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author 
 */
public class RamGenerator implements Generator {

    RamDB ramDB;
    protected int topN = -1;
    protected int maxRetry = Config.MAX_RETRY;

    public RamGenerator(RamDB ramDB) {
        this.ramDB = ramDB;
    }
    public int totalGenerate = 0;
    Iterator<Entry<String, CrawlDatum>> iterator;

    public CrawlDatum next() {
        if (topN >= 0) {
            if (totalGenerate >= topN) {
                return null;
            }
        }

        while (true) {
            if (iterator.hasNext()) {

                CrawlDatum datum = iterator.next().getValue();

                if (datum.getStatus() == CrawlDatum.STATUS_DB_FETCHED) {
                    continue;
                } else if (datum.getRetry() >= maxRetry) {
                    continue;
                } else {
                    totalGenerate++;
                    return datum;
                }

            } else {
                return null;
            }

        }
    }

    public void open() throws Exception {
        totalGenerate = 0;
        iterator = ramDB.crawlDB.entrySet().iterator();
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getTotalGenerate() {
        return totalGenerate;
    }

    public void close() throws Exception {

    }

}
