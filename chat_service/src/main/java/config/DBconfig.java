package config;

public class DBconfig {
	public final static String uri = "mongodb+srv://admin:swpNr4EPxAJHpwsp@chatmq.mp2ie.mongodb.net/chatmq?retryWrites=true&w=majority";
	public final static String dbName = "chatmq";
	public final static String roomsCollection = "Rooms";
	public final static String ROOMS = "Rooms";
	public final static String ID = "_id";
}

//
//  	public final static String uri ="mongodb://admin:swpNr4EPxAJHpwsp@chatmq-shard-00-00.mp2ie.mongodb.net:27017,chatmq-shard-00-01.mp2ie.mongodb.net:27017,chatmq-shard-00-02.mp2ie.mongodb.net:27017/chatmq?ssl=true&replicaSet=atlas-k8ay1j-shard-0&authSource=admin&retryWrites=true&w=majority"
//	public final static String uri = "mongodb+srv://admin:swpNr4EPxAJHpwsp@chatmq.mp2ie.mongodb.net/chatmq?retryWrites=false&w=majority";