package com.sda;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoTestD3 {

    public static void find() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");
        MongoCollection<Document> collection = database.getCollection("orders");

        FindIterable<Document> documents = collection.find(eq("_id", "1"));

        for (Document document : documents) {
            System.out.println(document.toJson());
            System.out.println(String.format("id=%s, customerName=%s, status=%s", document.getString("_id"), document.getString("customerName"), document.getString("status")));

            List<Document> orderDetails = (List<Document>) document.get("orderDetails");
            for (Document orderDetail : orderDetails) {
                System.out.println(String.format("productName=%s, qty=%s", orderDetail.getString("productName"), orderDetail.getInteger("qty")));
            }
        }
    }


    public static void delete() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");
        MongoCollection<Document> collection = database.getCollection("orders");
        collection.deleteOne(eq("_id", "1"));
    }

    public static void update() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");

        MongoCollection<Document> collection = database.getCollection("orders");
        collection.updateOne(eq("_id", "1"), new Document("$set", new Document("status", "CLOSED")));
    }

    public static void upsert() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");

        MongoCollection<Document> collection = database.getCollection("orders");

        Document orderDetail1 = new Document("productName", "T-Shirt").append("qty", 1);
        Document orderDetail2 = new Document("productName", "Socks").append("qty", 1);
        List<Document> details = new ArrayList<>();
        details.add(orderDetail1);
        details.add(orderDetail2);

        Document document = new Document("_id", "1")
                .append("status", "NEW")
                .append("customerName", "Piotr Zawadzki")
                .append("orderDetails", details);

        collection.replaceOne(eq("_id", "1"), document, new UpdateOptions().upsert(true));
    }

    public static void insert() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");

        MongoCollection<Document> collection = database.getCollection("orders");

        Document orderDetail1 = new Document("productName", "T-Shirt").append("qty", 1);
        Document orderDetail2 = new Document("productName", "Socks").append("qty", 1);
        List<Document> details = new ArrayList<>();
        details.add(orderDetail1);
        details.add(orderDetail2);

        Document document = new Document("_id", "1")
                .append("status", "NEW")
                .append("customerName", "Piotr Zawadzki")
                .append("orderDetails", details);

        collection.insertOne(document);
    }

    public static void createCollection() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("sda");

        database.createCollection("orders");
    }

    public static void main(String args[]) {
        upsert();

    }
}
