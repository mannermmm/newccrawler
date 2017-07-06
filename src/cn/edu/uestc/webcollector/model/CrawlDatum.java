package cn.edu.uestc.webcollector.model;

import cn.edu.uestc.webcollector.util.CrawlDatumFormater;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 爬取任务的数据结构
 * @author 
 */
public class CrawlDatum implements Serializable {

    public final static int STATUS_DB_UNFETCHED = 0;
    public final static int STATUS_DB_FETCHED = 1;

    private String url = null;
    private long fetchTime = System.currentTimeMillis();

    private int httpCode = -1;
    private int status = STATUS_DB_UNFETCHED;
    private int retry = 0;

    /**
     * 在WebCollector 2.5之后，不再根据URL去重，而是根据key去重
     * 可以通过getKey()方法获得CrawlDatum的key,如果key为null,getKey()方法会返回URL
     * 因此如果不设置key，爬虫会将URL当做key作为去重标准
     */
    private String key = null;

    /**
     * 在WebCollector 2.5之后，可以为每个CrawlDatum添加附加信息metaData
     * 附加信息并不是为了持久化数据，而是为了能够更好地定制爬取任务
     * 在visit方法中，可以通过page.getMetaData()方法来访问CrawlDatum中的metaData
     */
    private HashMap<String, String> metaData = new HashMap<String, String>();

    public CrawlDatum() {
    }

    public CrawlDatum(String url) {
        this.url = url;
    }

    public CrawlDatum(String url, String[] metas) throws Exception {
        this(url);
        if (metas.length % 2 != 0) {
            throw new Exception("length of metas must be even");
        } else {
            for (int i = 0; i < metas.length; i += 2) {
                putMetaData(metas[i * 2], metas[i * 2 + 1]);
            }
        }
    }

    public int incrRetry(int count) {
        retry = retry + count;
        return retry;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getUrl() {
        return url;
    }

    public CrawlDatum setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(HashMap<String, String> metaData) {
        this.metaData = metaData;
    }

    public CrawlDatum putMetaData(String key, String value) {
        this.metaData.put(key, value);
        return this;
    }

    public String getMetaData(String key) {
        return this.metaData.get(key);
    }

    public String getKey() {
        if (key == null) {
            return getUrl();
        } else {
            return key;
        }
    }

    public CrawlDatum setKey(String key) {
        this.key = key;
        return this;
    }
    
    
    
    @Override
    public String toString(){
        return CrawlDatumFormater.datumToString(this);
    }
    


}
