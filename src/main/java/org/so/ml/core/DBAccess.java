package org.so.ml.core;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.View;
import org.lightcouch.ViewResult;

import com.google.gson.JsonObject;

public class DBAccess {

	// Database
	private CouchDbClient dbClient;

	// View & ViewResult
	private View view;
	private ViewResult<String, String, JsonObject> viewResult;
	private ViewResult<String[], String, JsonObject> viewResultKeyArray;
	private ViewResult<String, String[], JsonObject> viewResultValueArray;
	private ViewResult<String[], String[], JsonObject> viewResultBothArrays;
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
	 * Close the DB Connection
	 */
	public void close() {
		// close db connection
		dbClient.shutdown();
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
	public boolean runView(String viewName, int stringArrayId) {
		try {
			// Run the given view
			view = dbClient.view(viewName);
			// Assign to public variables
			if(stringArrayId == 0) {
				viewResult = view.queryView(String.class, String.class, JsonObject.class);
				noOfRowsInView = viewResult.getTotalRows();
			}
			else if(stringArrayId == 1) {
				viewResultKeyArray = view.queryView(String[].class, String.class, JsonObject.class);
				noOfRowsInView = viewResultKeyArray.getTotalRows();
			}
			else if(stringArrayId == 2) {
				viewResultValueArray = view.queryView(String.class, String[].class, JsonObject.class);
				noOfRowsInView = viewResultValueArray.getTotalRows();
			}
			else if(stringArrayId == 3) {
				viewResultBothArrays = view.queryView(String[].class, String[].class, JsonObject.class);
				noOfRowsInView = viewResultBothArrays.getTotalRows();
			}
			else {
				throw new CouchDbException("For runView, stringArrayId can be either 0, 1, 2 or 3 depending if any of key, value are arrays");
			}
		}
		catch(CouchDbException excep) {
			System.out.println("Exception while running a view & assigning to viewResult:- ");
			System.out.println(viewName.toString());
			return false;
		}

		return true;
	}

	/**
	 * Runs the given View & filter with the key provided
	 * @param viewName
	 * @return true if run successfully else false
	 */
	public boolean runView(String viewName, int stringArrayId, String key) {
		try {
			// Run the given view
			view = dbClient.view(viewName).key(key);
			// Assign to public variables
			if(stringArrayId == 0) {
				viewResult = view.queryView(String.class, String.class, JsonObject.class);
				noOfRowsInView = viewResult.getTotalRows();
			}
			else if(stringArrayId == 1) {
				viewResultKeyArray = view.queryView(String[].class, String.class, JsonObject.class);
				noOfRowsInView = viewResultKeyArray.getTotalRows();
			}
			else if(stringArrayId == 2) {
				viewResultValueArray = view.queryView(String.class, String[].class, JsonObject.class);
				noOfRowsInView = viewResultValueArray.getTotalRows();
			}
			else if(stringArrayId == 3) {
				viewResultBothArrays = view.queryView(String[].class, String[].class, JsonObject.class);
				noOfRowsInView = viewResultBothArrays.getTotalRows();
			}
			else {
				throw new CouchDbException("For runView, stringArrayId can be either 0, 1, 2 or 3 depending if any of key, value are arrays");
			}
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
	public Object viewResultGetKey(int rowNo, int stringArrayId) {
		// create result string
		String result = "";
		String[] resultArray = {""};
		
		try {
			if(noOfRowsInView > rowNo) {
				if(stringArrayId == 0)
					result = viewResult.getRows().get(rowNo).getKey();
				else if(stringArrayId == 1)
					resultArray = viewResultKeyArray.getRows().get(rowNo).getKey();
				else if(stringArrayId == 2)
					result = viewResultValueArray.getRows().get(rowNo).getKey();
				else if(stringArrayId == 3)
					resultArray = viewResultBothArrays.getRows().get(rowNo).getKey();
				else {
					throw new CouchDbException("For viewResultGetKey, stringArrayId can be either 1, 2 or 3 depending if any of key, value are arrays");
				}
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
		catch(IndexOutOfBoundsException excep) {
			System.out.println("Index Out of Bounds! Accessing a result row which is not present.");
			return null;
		}

		// return
		if(stringArrayId == 0 || stringArrayId == 2)
			return result;
		else
			return resultArray;
	}

	/**
	 * Return Value using the id (from view result)
	 * @param rowNo
	 * @return
	 */
	public Object viewResultGetValue(int rowNo, int stringArrayId) {
		// create result string
		String result = "";
		String[] resultArray = {""};
		
		try {
			if(noOfRowsInView > rowNo) {
				if(stringArrayId == 0)
					result = viewResult.getRows().get(rowNo).getValue();
				else if(stringArrayId == 1)
					result = viewResultKeyArray.getRows().get(rowNo).getValue();
				else if(stringArrayId == 2)
					resultArray = viewResultValueArray.getRows().get(rowNo).getValue();
				else if(stringArrayId == 3)
					resultArray = viewResultBothArrays.getRows().get(rowNo).getValue();
				else {
					throw new CouchDbException("For viewResultGetKey, stringArrayId can be either 1, 2 or 3 depending if any of key, value are arrays");
				}
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
		catch(IndexOutOfBoundsException excep) {
			System.out.println("Index Out of Bounds! Accessing a result row which is not present.");
			return null;
		}

		// return
		if(stringArrayId == 0 || stringArrayId == 1)
			return result;
		else
			return resultArray;
	}
}
