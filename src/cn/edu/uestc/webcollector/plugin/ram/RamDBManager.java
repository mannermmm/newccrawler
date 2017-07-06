package cn.edu.uestc.webcollector.plugin.ram;

import cn.edu.uestc.webcollector.crawldb.DBManager;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.model.CrawlDatums;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author
 */
public class RamDBManager extends DBManager {

    Logger LOG = LoggerFactory.getLogger(DBManager.class);

    public RamDB ramDB;

    public RamDBManager(RamDB ramDB) {
        this.ramDB = ramDB;
    }

    @Override
    public boolean isDBExists() {
        return true;
    }

    @Override
    public void clear() throws Exception {
        ramDB.crawlDB.clear();
        ramDB.fetchDB.clear();
        ramDB.linkDB.clear();
        ramDB.redirectDB.clear();
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        String key = datum.getKey();
        if (!force) {
            if (ramDB.crawlDB.containsKey(key)) {
                return;
            }
        }
        ramDB.crawlDB.put(key, datum);
    }

    @Override
    public void merge() throws Exception {
        LOG.info("start merge");

        /*合并fetch库*/
        LOG.info("merge fetch database");
        for (Entry<String, CrawlDatum> fetchEntry : ramDB.fetchDB.entrySet()) {
            ramDB.crawlDB.put(fetchEntry.getKey(), fetchEntry.getValue());
        }

        /*合并link库*/
        LOG.info("merge link database");
        for (String key : ramDB.linkDB.keySet()) {
            if (!ramDB.crawlDB.containsKey(key)) {
                ramDB.crawlDB.put(key, ramDB.linkDB.get(key));
            }
        }

        LOG.info("end merge");

        ramDB.fetchDB.clear();
        LOG.debug("remove fetch database");
        ramDB.linkDB.clear();
        LOG.debug("remove link database");

    }

    public void initSegmentWriter() throws Exception {
    }

    public void wrtieFetchSegment(CrawlDatum fetchDatum) throws Exception {
        ramDB.fetchDB.put(fetchDatum.getKey(), fetchDatum);
    }

    public void wrtieParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum datum : parseDatums) {
            ramDB.linkDB.put(datum.getKey(), datum);
        }
    }

    public void closeSegmentWriter() throws Exception {
    }

    public void lock() throws Exception {
    }

    public boolean isLocked() throws Exception {
        return false;
    }

    public void unlock() throws Exception {
    }

    public void writeRedirectSegment(CrawlDatum datum, String realUrl) throws Exception {
        String key = datum.getKey();
        ramDB.redirectDB.put(key, realUrl);
    }

}
