package org.so.ml.PredictTags;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

public class SaveQuestion {

	public static void main(String[] args) {

		// communicate and write to couchdb 
		CouchDbClient dbClient = new CouchDbClient("couchdb.properties");
		
		JsonObject json = new JsonObject();
//		String tagArray[] = {"C++", "Multithreading", "Qt", "QML"};
		String t1 = "javascript";
		String t2 = "ent2";
		String t3 = "coffee";
		String t4 = "html";
		String t5 = "chrome";
//		System.out.println(tagArray);
		json.addProperty("tag1", t1);
		json.addProperty("tag2", t2);
		json.addProperty("tag3", t3);
		json.addProperty("tag4", t4);
		json.addProperty("tag5", t5);
		json.addProperty("question", "How to work with coffee script?");
		
		Response r = dbClient.save(json);
		System.out.println(r.toString());
		
//		System.out.println(findObj.get("question"));
	}
}
