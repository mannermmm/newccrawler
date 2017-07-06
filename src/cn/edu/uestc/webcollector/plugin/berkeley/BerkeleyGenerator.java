package cn.edu.uestc.webcollector.plugin.berkeley;

import cn.edu.uestc.webcollector.crawldb.Generator;
import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.util.Config;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 
 */
public class BerkeleyGenerator implements Generator {

    public static final Logger LOG = LoggerFactory.getLogger(BerkeleyGenerator.class);

    Cursor cursor = null;
    Database crawldbDatabase = null;
    Environment env=null;
    protected int totalGenerate = 0;
    protected int topN = -1;
    protected int maxRetry = Config.MAX_RETRY;
    String crawlPath;

    public BerkeleyGenerator(String crawlPath) {
        this.crawlPath = crawlPath;

    }

    public void open() throws Exception {
        File dir = new File(crawlPath);
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        env = new Environment(dir, environmentConfig);
        totalGenerate = 0;

    }

    public void close() throws Exception {

        if(cursor!=null){
            cursor.close();
        }
        cursor = null;
        if(crawldbDatabase!=null){
            crawldbDatabase.close();
        }
        if(env!=null){
            env.close();
        }
    }

    public DatabaseEntry key = new DatabaseEntry();
    public DatabaseEntry value = new DatabaseEntry();

    public CrawlDatum next() {
        if (topN >= 0) {
            if (totalGenerate >= topN) {
                return null;
            }
        }

        if (cursor == null) {
            crawldbDatabase = env.openDatabase(null, "crawldb", BerkeleyDBUtils.defaultDBConfig);
            cursor = crawldbDatabase.openCursor(null, CursorConfig.DEFAULT);
        }

        while (true) {
            if (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

                try {
                    CrawlDatum datum = BerkeleyDBUtils.createCrawlDatum(key, value);
                    if (datum.getStatus() == CrawlDatum.STATUS_DB_FETCHED) {
                        continue;
                    } else {
                        if (datum.getRetry() >= maxRetry) {
                            continue;
                        }
                        totalGenerate++;
                        return datum;
                    }
                } catch (Exception ex) {
                    LOG.info("Exception when generating", ex);
                    continue;
                }
            } else {
                return null;
            }
        }
    }

    public int getTotalGenerate() {
        return totalGenerate;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

}
