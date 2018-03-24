package com.sda;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.util.ArrayList;
import java.util.List;

public class MongoTest {

    public static void find() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("sda");
        DBCollection collection = database.getCollection("orders");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", "2");
        DBCursor dbCursor = collection.find(query);
        while(dbCursor.hasNext()) {
            DBObject next = dbCursor.next();
            System.out.println(next);
            System.out.println(String.format("id=%s, customerName=%s, status=%s", next.get("_id"), next.get("customerName"), next.get("status")));
            BasicDBList details = (BasicDBList) next.get("orderDetails");
            for(Object object: details) {
                BasicDBObject detail = (BasicDBObject) object;
                System.out.println(String.format("productName=%s, qty=%s", detail.get("productName"), detail.get("qty")));
            }
        }
    }


    public static void delete() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("sda");
        DBCollection collection = database.getCollection("orders");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", "1");
        collection.remove(query);
    }

    public static void update() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("sda");
        DBCollection collection = database.getCollection("orders");
        BasicDBObject query = new BasicDBObject();
        query.put("status", "NEW");
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("customerName", "Jan Nowak");
        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);
        collection.update(query, updateObject);

    }

    public static void upsert() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB database = mongoClient.getDB("sda");

        DBCollection collection = database.getCollection("orders");

        BasicDBObject document = new BasicDBObject();
        document.put("_id", "1");
        document.put("status", "NEW");
        document.put("customerName", "Piotr Zawadzki");

        BasicDBObject orderDetail1 = new BasicDBObject();
        orderDetail1.put("productName", "T-Shirt");
        orderDetail1.put("qty", 1);

        BasicDBObject orderDetail2 = new BasicDBObject();
        orderDetail2.put("productName", "Socks");
        orderDetail2.put("qty", 1);

        List<BasicDBObject> orderDetails = new ArrayList<>();
        orderDetails.add(orderDetail1);
        orderDetails.add(orderDetail2);

        document.put("orderDetails", orderDetails);


        BasicDBObject query = new BasicDBObject();
        query.put("_id", "1");

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", document);
        collection.update(query, updateObject, true, false);
    }

    public static void main(String args[]) {
        find();

    }
}
