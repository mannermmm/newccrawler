package cn.edu.uestc.webcollector.crawldb;

/**
 *
 * @author 
 */
public interface DBLock {

    public void lock() throws Exception;

    public boolean isLocked() throws Exception;

    public void unlock() throws Exception;
}
