package cn.edu.uestc.webcollector.plugin.berkeley;

import cn.edu.uestc.webcollector.model.CrawlDatum;
import cn.edu.uestc.webcollector.util.CrawlDatumFormater;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author 
 */
public class BerkeleyDBUtils {
    public static DatabaseConfig defaultDBConfig;
    
    static{
        defaultDBConfig=createDefaultDBConfig();
    }
    public static DatabaseConfig  createDefaultDBConfig(){
        DatabaseConfig databaseConfig=new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setDeferredWrite(true);
        return databaseConfig;
    }
    
    public static void writeDatum(Database database,CrawlDatum datum) throws Exception{
        String key=datum.getKey();
        String value=CrawlDatumFormater.datumToJsonStr(datum);
        put(database,key,value);
    }
    
    public static void put(Database database,String key,String value) throws Exception{
        database.put(null, strToEntry(key), strToEntry(value));
    }
    
    public static DatabaseEntry strToEntry(String str) throws UnsupportedEncodingException{
        return new DatabaseEntry(str.getBytes("utf-8"));
    }
    
     public static CrawlDatum createCrawlDatum(DatabaseEntry key,DatabaseEntry value) throws Exception{
        String datumKey=new String(key.getData(),"utf-8");
        String valueStr=new String(value.getData(),"utf-8");
        return CrawlDatumFormater.jsonStrToDatum(datumKey, valueStr);
    }
}
