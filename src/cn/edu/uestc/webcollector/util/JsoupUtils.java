package cn.edu.uestc.webcollector.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 *
 * @author 
 */
public class JsoupUtils {
    public static void makeAbs(Document doc,String url){
        if(url!=null){
            doc.setBaseUri(url);
        }
        doc.traverse(new NodeVisitor() {

            public void head(Node node, int i) {
                if(node instanceof Element){
                    Element tag=(Element) node;
                    if(tag.hasAttr("href")){
                        String absHref=tag.attr("abs:href");
                        tag.attr("href",absHref);
                    }
                    if(tag.hasAttr("src")){
                        String absSrc=tag.attr("abs:src");
                        tag.attr("src",absSrc);
                    }
                }
            }

            public void tail(Node node, int i) {
            }
        });
    }
}
