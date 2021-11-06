package services;


import com.mongodb.ConnectionString;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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
		ConnectionString connectionString = new ConnectionString(uri);
		this.mongoClient = MongoClients.create(connectionString);
		this.database = mongoClient.getDatabase(dbName);
		this.collection = database.getCollection(roomsCollection);
	}

	public void insert(Document document){
		collection.insertOne(document).subscribe(new Subscriber<InsertOneResult>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(InsertOneResult insertOneResult) {
				System.out.println(insertOneResult);
			}

			@Override
			public void onError(Throwable t) {
				try {
					throw new ExecutionException(t);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onComplete() {
				System.out.println("Inserted!");
			}
		});
	}

	public UpdateResult update(Document document, Document update) throws ExecutionException, InterruptedException {
		CompletableFuture<UpdateResult> future = new CompletableFuture<>();
		collection.updateOne(document, new Document("$set",update)).subscribe(new Subscriber<UpdateResult>() {
			UpdateResult result;
			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(UpdateResult updateResult) {
				result = updateResult;
			}

			@Override
			public void onError(Throwable t) {
				future.completeExceptionally(t);
			}

			@Override
			public void onComplete() {
				future.complete(result);
			}
		});
//		, (result, t) -> {
//			if(result != null) future.complete(result);
//			else future.completeExceptionally(t);
//		});
		return future.get();
	}

	public String getRooms(Document document) throws ExecutionException, InterruptedException {
		CompletableFuture<String> future = new CompletableFuture<>();

		collection.find(document).first().subscribe(new Subscriber<Document>() {
			Document result;

			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(Document document) {
				 result = document;
			}

			@Override
			public void onError(Throwable t) {
				future.completeExceptionally(t);
			}

			@Override
			public void onComplete() {
				future.complete(result.get("rooms").toString());
			}
		});

//	(result, t) -> {
//			if(result != null) future.complete(result.get("rooms").toString());
//			else future.completeExceptionally(t);
//		});
		return future.get();
	}
}
