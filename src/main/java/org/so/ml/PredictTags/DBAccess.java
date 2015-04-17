package org.so.ml.PredictTags;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.View;
import org.lightcouch.ViewResult;

import com.google.gson.JsonObject;

public class DBAccess {

	// Database
	CouchDbClient dbClient;
	
	// View & ViewResult
	View view;
	ViewResult<String, String, JsonObject> viewResult;
	ViewResult<String[], String, JsonObject> viewResultArray;
	public long noOfRowsInView = 0;

	/**
	 * Connect to the Database using properties file
	 * @param propFile
	 * @return
	 */
	public int connect(String propFile) {
		// Connect to the Database
		dbClient = new CouchDbClient(propFile);
		// return
		if(dbClient != null) {
			return 1;
		}
		else {
			return 0;
		}
	}

	/**
	 * Insert the Json Object into DB
	 * @param jsonO
	 * @return
	 */
	public boolean save(JsonObject jsonO) {
		try {
			// Insert json into DB
			dbClient.save(jsonO);
		}
		catch(CouchDbException excep) {
			System.out.println("Exception while saving json to DB");
			System.out.println(excep.toString());
			return false;
		}
		// If no exception return true
		return true;
	}

	/**
	 * Runs the given View
	 * @param viewName
	 * @return true if run successfully else false
	 */
	public boolean runView(String viewName) {
		try {
			// Run the given view
			view = dbClient.view(viewName);
			// Assign to public variables
			viewResult = view.queryView(String.class, String.class, JsonObject.class);
			noOfRowsInView = viewResult.getTotalRows();
		}
		catch(CouchDbException excep) {
			System.out.println("Exception while running a view & assigning to viewResult:- ");
			System.out.println(viewName.toString());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return key using the id (from view result)
	 * @param rowNo
	 * @return
	 */
	public String viewResultGetKey(int rowNo) {
		// create result string
		String result = "";
		try {
			if(noOfRowsInView > rowNo) {
				result = viewResult.getRows().get(rowNo).getKey();
			}
			else {
				throw new CouchDbException("Accessing a result row which is not present.\n Index Out of Bounds!\n");
			}
		}
		catch(CouchDbException excep) {
			System.out.println("Exception in Getting Key from Result Rows");
			System.out.println(excep);
			return null;
		}
		
		// return
		return result;
	}
	
	/**
	 * Return Value using the id (from view result)
	 * @param rowNo
	 * @return
	 */
	public String viewResultGetValue(int rowNo) {
		// create result string
		String result = "";
		try {
			if(noOfRowsInView > rowNo) {
				result = viewResult.getRows().get(rowNo).getValue();
			}
			else {
				throw new CouchDbException("Accessing a result row which is not present.\n Index Out of Bounds!\n");
			}
		}
		catch(CouchDbException excep) {
			System.out.println("Exception in Getting Key from Result Rows");
			System.out.println(excep);
			return null;
		}
		
		// return
		return result;
	}
}
