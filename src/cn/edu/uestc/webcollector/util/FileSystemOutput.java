package cn.edu.uestc.webcollector.util;

import cn.edu.uestc.webcollector.model.Page;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileSystemOutput并不属于WebCollector内核，它只是实现一个 简单的输出，将网页根据url路径，保存到本地目录，按照网站目录
 * 结构来存储网站内容。BreadthCrawler的visit函数中，默认使用
 * FileSystemOutput来保存网页。不推荐使用FileSystemOutput来 存储网页
 *
 * @author 
 */
public class FileSystemOutput {

    public static final Logger LOG = LoggerFactory.getLogger(FileSystemOutput.class);

    protected String root;

    public FileSystemOutput(String root) {
        this.root = root;
    }

    public void output(Page page) {
        try {
            URL _URL = new URL(page.getUrl());
            String query = "";
            if (_URL.getQuery() != null) {
                query = "_" + _URL.getQuery();
            }
            String path = _URL.getPath();
            if (path.length() == 0) {
                path = "index.html";
            } else {
                if (path.endsWith("/")) {
                    path = path + "index.html";
                } else {
                    int lastSlash = path.lastIndexOf("/");
                    int lastPoint = path.lastIndexOf(".");
                    if (lastPoint < lastSlash) {
                        path = path + ".html";
                    }
                }
            }
            path += query;
            File domain_path = new File(root, _URL.getHost());
            File f = new File(domain_path, path);
            FileUtils.writeFileWithParent(f, page.getContent());
            LOG.info("output " + f.getAbsolutePath());
        } catch (Exception ex) {
            LOG.info("Exception", ex);
        }
    }
    

}
