package org.so.ml.PredictTags;
import org.so.ml.core.DBAccess;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	// communicate and write to couchdb 
//		CouchDbClient dbClient = new CouchDbClient("couchdb.properties");
//		View v = dbClient.view("ent2/all_ent2_docs");
//		ViewResult<int[], String, String> entries = v.queryView(int[].class, String.class, String.class);
//		List<JsonObject> l = dbClient.view("ent2/all_ent2_docs").query(JsonObject.class);
//		for(int i=0; i<l.size(); i++)
//			System.out.println(l.get(i).get("key"));
//		JsonObject json = new JsonObject();
//		json.addProperty("_id", "java");
//		json.addProperty("count", 2);
//		dbClient.save(json);
		
		// update the element
//		JsonObject upJson = dbClient.find(JsonObject.class, "java");
//		System.out.println(upJson.get("count"));
//		upJson.addProperty("count", upJson.get("count").getAsInt()+1);
//		dbClient.save(upJson);
    	
    	// Get DB class
    	DBAccess db = new DBAccess();
    	db.connect("couchdb.properties");
    	
    	// view
    	db.runView("idfs/get_idf", 0, "sub");
    	
    	// get result
    	System.out.println("No of rows: "+db.noOfRowsInView);
    	System.out.println(db.viewResultGetValue(0, 0));
//    	System.out.println(db.viewResultGetValue(1, 0));
    }
}
