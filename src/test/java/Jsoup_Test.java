import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * @Auther:http://www.bjsxt.com
 * @Date:2019/7/26
 * @Description:PACKAGE_NAME
 * @version:1.0
 */
public class Jsoup_Test {


    public static MongoCollection<org.bson.Document> getCollection(String dbName, String collectionName) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<org.bson.Document> collection = database.getCollection(collectionName);
        return collection;
    }
    private static MongoCollection<org.bson.Document> collection = getCollection("movie", "info");

    @Test
    public void crawler() throws IOException {
        int page = 1;
        int result = 0;
        for (int i = 1; i <= page; i++) {
            result = crawler_page("https://www.dytt8.net/html/gndy/jddy/list_63_" + i + ".html");
        }
        System.out.println("共爬到" + result * page + "条电影数据");
    }

    private int crawler_page(String result) throws IOException {
        Document doc = null;
        //模拟火狐浏览器
        doc = Jsoup.connect(result).userAgent("Mozilla").get();
        //获取详情页url
        Elements div = doc.getElementsByClass("co_content8");
        Elements table = div.select("table");
        int size = table.size();
        for (Element element : table) {
//            try {
////                模仿人手动点击
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Elements tr = element.select("tr");
            //获取链接和标题
            String href = tr.get(1).select("a").attr("abs:href");
            String title = tr.get(1).select("a").text();
            //要过滤掉首页地址
//            if ("http://www.dytt8.net/html/gndy/jddy/index.html".equals(href)){
//                continue;
//            }
            //访问详情页链接
            doc = Jsoup.connect(href).userAgent("Mozilla").get();
            Element zoom = doc.getElementById("Zoom");
            Elements p = zoom.select("p");
            String movie_describe = p.text();
            Elements img = zoom.select("img[src$=.jpg]");
            String img_src = img.get(0).attr("abs:src");
            //很简略的获取下载地址，这种方式适用情况需要研究。
            String download_url = zoom.select("td").text();

            org.bson.Document document = new org.bson.Document("type", "最新电影")
                    .append("title", title)
                    .append("movie_describe",movie_describe)
                    .append("img_src",img_src)
                    .append("download_url",download_url);
            collection.insertOne(document);
        }
        return size;
    }
}
