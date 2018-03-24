package com.sda;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MyMongoTest {
    public static void main(String[] args) {
        try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {
            MongoDatabase database = mongoClient.getDatabase("sda");
            //database.createCollection("orders");
            createSampleDocument(database);
            updateStatus(database, 1, "DELIVERED");
            getDocumentsByStatus(database,"DELIVERED");
            deleteOrder(database, 1);
        }
    }

    private static void getDocumentsByStatus(MongoDatabase database, String status) {
        MongoCollection<Document> collection = database.getCollection("orders");
        FindIterable<Document> documents = collection.find(eq("status", status));
        for(Document document: documents) {
            System.out.println(document.toJson());
            System.out.println(String.format("id=%d, customerName=%s, status=%s",
                    document.getInteger("_id"), document.getString("customerName"), document.getString("status")));
            List<Document> orderDetails = (List<Document>) document.get("orderDetails");
            for(Document orderDetail: orderDetails) {
                System.out.println(String.format("productName=%s, quantity=%d", orderDetail.getString("productName"), orderDetail.getInteger("quantity")));
            }
        }
    }

    private static void createSampleDocument(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("orders");
        System.out.println("NUMBER OF DOCUMENTS IN COLLECTION: " + collection.count());

        Document orderDetail1 = new Document("productName", "T-shirt").append("quantity", 1).append("productPrice", 100.99);
        Document orderDetail2 = new Document("productName", "REKAWICE NARCIARSKIE").append("quantity", 2).append("productPrice", 110.00);

        List<Document> details = new ArrayList<>();
        details.add(orderDetail1);
        details.add(orderDetail2);

        Document order = new Document("_id", 1).append("customerName", "Piotr Zawadzki").append("customerStreet", "Rewolucji 77").append("customerCity", "Lodz")
                .append("customerPostalCode", "92-243").append("deliverStreet", "Rewolucji 77").append("deliveryCity", "Lodz").append("deliveryPostalCode", "92-243")
                .append("status", "NEW").append("creationDate", new Date()).append("orderDetails", details);

        //collection.insertOne(order); //IF document with id 1 exist then throw exception
        collection.replaceOne(eq("_id", 1), order, new UpdateOptions().upsert(true)); //UPSERT
    }

    private static void updateStatus(MongoDatabase database, int documentId, String newStatus) {
        MongoCollection<Document> collection = database.getCollection("orders");
        collection.updateOne(eq("_id", documentId),
                new Document("$set", new Document("status", newStatus).append("customerName", "Jan Kowalski").append("newField", "TEST")));
    }


    public static void deleteOrder(MongoDatabase database, int documentId) {
        MongoCollection<Document> collection = database.getCollection("orders");
        collection.deleteOne(eq("_id", documentId));
    }

}
