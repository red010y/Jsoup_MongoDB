import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther:http://www.bjsxt.com
 * @Date:2019/7/29
 * @Description:PACKAGE_NAME
 * @version:1.0
 */
public class Mongo_Test {

    private static MongoCollection<Document> collection= getCollection("test", "people");

    public static MongoCollection<Document> getCollection(String dbName , String collectionName){
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }
    @Test
    public void insert(){
        Document document1 = new Document("sname", "jiang").append("sage", 22);
        Document document2 = new Document("sname", "tian").append("sage", 21);
        List<Document> list=new ArrayList<Document>();
        list.add(document1);
        list.add(document2);
        collection.insertMany(list);
//        collection.insertOne(document);
    }
    @Test
    public void find(){
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()){
            System.out.println(cursor.next().toJson());
        }
    }
    @Test
    public void delete(){
        collection.deleteOne(Filters.eq("sname","jiang"));
        collection.deleteMany(Filters.eq("sage",21));
    }
    @Test
    public void update(){
        collection.updateOne(Filters.eq("sname","jiang"),new Document("$set",new Document("sage",18)));
        collection.updateMany(Filters.eq("sname","tian"),new Document("$set",new Document("sage",18)));
    }
}
