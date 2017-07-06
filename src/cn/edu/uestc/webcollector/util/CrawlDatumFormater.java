package cn.edu.uestc.webcollector.util;

import cn.edu.uestc.webcollector.model.CrawlDatum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author 
 */
public class CrawlDatumFormater {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String datumToString(CrawlDatum datum) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nKEY: ").append(datum.getKey())
                .append("\nURL: ").append(datum.getUrl())
                .append("\nFetchTime:").append(sdf.format(new Date(datum.getFetchTime())))
                .append("\nHttpCode:").append(datum.getHttpCode())
                .append("\nRETRY:").append(datum.getRetry())
                .append("\nSTATUS：");
       
        if (datum.getStatus() == CrawlDatum.STATUS_DB_FETCHED) {
            sb.append("fetched");
        } else {
            sb.append("unfeteched");
        }
        int metaIndex=0;
        
        for (Entry<String, String> entry : datum.getMetaData().entrySet()) {
            sb.append("\nMETA").append("[").append(metaIndex++).append("]:(")
                    .append(entry.getKey()).append(",").append(entry.getValue()).append(")");
        }
        sb.append("\n");
        return sb.toString();
    }

    public static CrawlDatum jsonStrToDatum(String crawlDatumKey, String str) {
        JSONArray jsonArray = new JSONArray(str);
        CrawlDatum crawlDatum = new CrawlDatum();
        crawlDatum.setKey(crawlDatumKey);
        crawlDatum.setUrl(jsonArray.getString(0));
        crawlDatum.setStatus(jsonArray.getInt(1));
        crawlDatum.setFetchTime(jsonArray.getLong(2));
        crawlDatum.setRetry(jsonArray.getInt(3));
        crawlDatum.setHttpCode(jsonArray.getInt(4));
        if (jsonArray.length() == 6) {
            JSONObject metaJSONObject = jsonArray.getJSONObject(5);
            for (Object keyObject : metaJSONObject.keySet()) {
                String key = keyObject.toString();
                String value = metaJSONObject.getString(key);
                crawlDatum.putMetaData(key, value);
            }
        }
        return crawlDatum;
    }

    public static String datumToJsonStr(CrawlDatum datum) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(datum.getUrl());
        jsonArray.put(datum.getStatus());
        jsonArray.put(datum.getFetchTime());
        jsonArray.put(datum.getRetry());
        jsonArray.put(datum.getHttpCode());
        if (!datum.getMetaData().isEmpty()) {
            jsonArray.put(new JSONObject(datum.getMetaData()));
        }
        return jsonArray.toString();
    }
}
