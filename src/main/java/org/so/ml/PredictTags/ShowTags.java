package org.so.ml.PredictTags;
//import org.lightcouch.Document;
import org.so.ml.WebScraper.WebsiteScraper;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public class ShowTags {

	public static void main(String[] args) throws ResponseException, NotFound {
		
		// agent
		UserAgent agent = new UserAgent();
		agent.visit("http://stackoverflow.com/tags");
		com.jaunt.Document doc = agent.doc;
		// TODO Auto-generated method stub
		WebsiteScraper ws = new WebsiteScraper();
		System.out.println(ws.getAllTags(doc));
	}

}
