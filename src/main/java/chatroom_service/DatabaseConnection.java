package chatroom_service;


import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.InsertOneOptions;
import org.bson.Document;


public class DatabaseConnection {

	private final static String uri = "mongodb://admin:chatmq17@ds247191.mlab.com:47191/chatmq";
	private final static String dbName = "chatmq";
	private final static String roomsCollection = "Rooms";

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;

	public DatabaseConnection(){
		this.mongoClient = MongoClients.create(uri);
		this.database = mongoClient.getDatabase(dbName);
		this.collection = database.getCollection(roomsCollection);
	}

	public void insert(Document doc){
		collection.insertOne(doc, (result, t) -> System.out.println("Inserted!"));
	}

}
