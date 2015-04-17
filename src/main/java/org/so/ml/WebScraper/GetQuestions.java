package org.so.ml.WebScraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.JsonObject;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public class GetQuestions {
	
	// No of questions to fetch in total for a tag
	private int totalQsPerTag = 60;
	// DB properties string
	private String dbProps = "couchdb.properties";
	// DB Object
	private CouchDbClient dbClient; 
	
	/**
	 * Constructor
	 */
	public GetQuestions() {
		// Connect to the Database
		this.dbClient = new CouchDbClient(dbProps);
	}
	
	/**
	 * getStopWords method gets the English stop words from a file and returns
	 * them as a list
	 * 
	 * @return stop words list generated from the file
	 * @throws IOException
	 */
	private List<String> getStopWords() throws IOException {
		List<String> stopWordsList = new ArrayList<String>();
		String stopWordsFile = "./data/StopWords.txt";
		BufferedReader bReader = new BufferedReader(new FileReader(stopWordsFile));
		String line;
		while ((line = bReader.readLine()) != null) {
			stopWordsList.add(line.toString().trim());
		}
		bReader.close();
		return stopWordsList;
	}
	
	/**
	 * Push given json to DB
	 * @param qJson
	 * @return
	 */
	private boolean pushToDB(JsonObject qJson) {
		// push json object to database
		Response r = this.dbClient.save(qJson);
		// return 
		if(r.getError() == null)
			return true;
		else {
			System.out.println("Error: "+r.getError() + "Q: "+qJson.get("description"));
			return false;
		}
	}

	/**
	 * @param args
	 * @throws ResponseException 
	 * @throws NotFound 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ResponseException, NotFound, IOException {
		
		// Create GetQuestions Object
		GetQuestions getQs = new GetQuestions();

		// List of tags to get questions from
		List<String> tagList = new ArrayList<String>();
		tagList.add("python");
		
		// Construct the URL
		String urlMain = "http://stackoverflow.com/questions/tagged/";
		
		// create view agent
		UserAgent agent = new UserAgent();
		
		// Iterate over the tags to get the pages
		Iterator<String> tagIterator = tagList.iterator();
		while(tagIterator.hasNext()) {
			// Append the URL
			urlMain = urlMain + tagIterator.next();
			
			// Get Questions
			for(int page = 3; page <= getQs.totalQsPerTag/15; ++page) {
				//Append the URL
				urlMain += "?page="+ page +"&sort=newest&pagesize=15";
				// visit the webpage
				agent.visit(urlMain);
				// webpage document
				Document webDoc = agent.doc;
				// Create Web scrapper
				WebsiteScraper webScraper;
				// Find question summary
				Elements questions = webDoc.findEvery("<div class=question-summary>");
				Iterator<Element> questionsIterator = questions.iterator();
				while (questionsIterator.hasNext()) {
					Element question = questionsIterator.next();
					String link = question.findFirst("<a>").getAt("href").toString();
					agent.visit(link);
					// Send document to web scrapper to get question, tags & code parts
					webScraper = new WebsiteScraper(agent.doc, getQs.getStopWords());
					
					// Generate a Json object to put into database
					JsonObject qJson = new JsonObject();
					
					// Fill the qJson
					qJson.addProperty("type", "question");
					// Add Tags
					List<String> qTags = webScraper.qTags;
					for(int t=1; t<=qTags.size(); t++) {
						qJson.addProperty("tag"+t, qTags.get(t-1));
					}
					// Add Question
					List<String> qDescription = webScraper.qDescriptionTokens;
					String description = ""; 
					for(int d=0; d<qDescription.size(); ++d) {
						description += qDescription.get(d) + " ";
					}
					qJson.addProperty("description", description);
					// Add Code Block
					List<String> qCode = webScraper.qCodeTokens;
					String code = ""; 
					for(int d=0; d<qCode.size(); ++d) {
						code += qCode.get(d) + " ";
					}
					qJson.addProperty("code", code);
					
					// Add this to DB
					boolean res = getQs.pushToDB(qJson);
					if(!res)
						System.out.println("Error!");
				}
			}
		}
	}
}
