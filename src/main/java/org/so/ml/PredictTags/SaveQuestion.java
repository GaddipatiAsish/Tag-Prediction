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
		String t1 = "js";
		String t2 = "ent2";
		String t3 = "java";
		String t4 = "iOS";
		String t5 = "chrome";
//		System.out.println(tagArray);
		json.addProperty("tag1", t1);
		json.addProperty("tag2", t2);
		json.addProperty("tag3", t3);
		json.addProperty("tag4", t4);
		json.addProperty("tag5", t5);
		json.addProperty("question", "Trying to read ext2 fs. But gives some read error when tried to read bytes.");
		
		Response r = dbClient.save(json);
		System.out.println(r.toString());
		
//		System.out.println(findObj.get("question"));
	}
}
