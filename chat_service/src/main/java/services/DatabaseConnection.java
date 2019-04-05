package services;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static config.DBconfig.*;

/**
 * @author Monica Gondolini
 */
public class DatabaseConnection {

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;

	public DatabaseConnection(){
		this.mongoClient = MongoClients.create(uri);
		this.database = mongoClient.getDatabase(dbName);
		this.collection = database.getCollection(roomsCollection);
	}

	public void insert(Document document){
		collection.insertOne(document, (result, t) -> System.out.println("Inserted!"));
	}

	public UpdateResult update(Document document, Document update) throws ExecutionException, InterruptedException {
		CompletableFuture<UpdateResult> future = new CompletableFuture<>();
		collection.updateOne(document, new Document("$set",update), (result, t) -> {
			if(result != null) future.complete(result);
			else future.completeExceptionally(t);
		});
		return future.get();
	}

	public String getRooms(Document document) throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = new CompletableFuture<>();
		collection.find(document).first((result, t) -> {
			if(result != null) future.complete(result.get("rooms").toString());
			else future.completeExceptionally(t);
		});
		return future.get();
	}

}
