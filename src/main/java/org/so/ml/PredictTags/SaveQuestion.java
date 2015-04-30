package org.so.ml.PredictTags;

import org.lightcouch.CouchDbClient;
//import org.lightcouch.Response;
import org.lightcouch.View;
import org.lightcouch.ViewResult;

import com.google.gson.JsonObject;
//import com.google.gson.JsonPrimitive;

public class SaveQuestion {
	
	// communicate and write to couchdb 
	public static CouchDbClient dbClient = new CouchDbClient("couchdb.properties");

	public static void main(String[] args) {

//		delete();
		
//		JsonObject json = new JsonObject();
////		String tagArray[] = {"C++", "Multithreading", "Qt", "QML"};
//		String t1 = "javascript";
//		String t2 = "ent2";
//		String t3 = "coffee";
//		String t4 = "html";
//		String t5 = "chrome";
////		System.out.println(tagArray);
//		json.addProperty("tag1", t1);
//		json.addProperty("tag2", t2);
//		json.addProperty("tag3", t3);
//		json.addProperty("tag4", t4);
//		json.addProperty("tag5", t5);
//		json.addProperty("question", "Can Asish see this?");
//		
//		Response r = dbClient.save(json);
//		System.out.println(r.toString());
//		System.out.println(r.getError());
		
//		System.out.println(findObj.get("question"));
		
		// --------
		View v = dbClient.view("delete/tag_vector"); // update/python -- delete/empty_qcontent
		ViewResult<String, String, JsonObject> vResult = v.queryView(String.class, String.class, JsonObject.class);
		for(int i=0, max=(int) vResult.getTotalRows(); i<max; i++) {
			delete(vResult.getRows().get(i).getKey(), vResult.getRows().get(i).getValue());
		}
		
//		// --------
//		View v = dbClient.view("delete/update_to_cpp"); // update/python -- delete/empty_qcontent
//		ViewResult<String[], String[], JsonObject> vResult = v.queryView(String[].class, String[].class, JsonObject.class);
//		for(int i=0, max=(int) vResult.getTotalRows(); i<max; i++) {
//			update(vResult.getRows().get(i).getKey(), vResult.getRows().get(i).getValue());
//		}
	}
	
	// Delete given document
	public static void delete(String key, String value) {
		// 
		dbClient.remove(key, value);
		System.out.println("Deleted: "+key+" - "+value);
	}
	
	// Update
	public static void update(String[] key, String[] value) {
		JsonObject json = new JsonObject();
		json.addProperty("_id", key[0]);
		json.addProperty("_rev", key[1]);
		json.addProperty("type", key[2]);
		json.addProperty("qContent", key[3]);
		
		// find the problematic tags to update 
		for(int i=0; i<5; i++) {
			if(value[i] != null) {
				if(value[i].equals("c++"))
					json.addProperty("tag"+(i+1), "cpp");
				else if(value[i].equals("c++11"))
					json.addProperty("tag"+(i+1), "cpp11");
				else if(value[i].equals("visual-c++"))
					json.addProperty("tag"+(i+1), "visual-cpp");
				else
					json.addProperty("tag"+(i+1), value[i]);
			}
		}
		// update
		dbClient.update(json);
	}
}
