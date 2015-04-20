package org.so.ml.WebScraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.JsonObject;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

/**
 * GetQuestions gets the questions that are related to a tag and loads them into
 * database by removing the stop words.
 * 
 * Second Step of the Project
 * 
 *
 */
public class GetQuestions {

	// No of questions to fetch in total for a tag
	private int totalQsPerTag;
	// DB properties string
	private String dbProps = "couchdb.properties";
	// DB Object
	private CouchDbClient dbClient;

	/**
	 * Constructor
	 */
	public GetQuestions(int totalQsPerTag) {
		// Total questions per tag
		this.totalQsPerTag = totalQsPerTag;
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
		BufferedReader bReader = new BufferedReader(new FileReader(
				stopWordsFile));
		String line;
		while ((line = bReader.readLine()) != null) {
			stopWordsList.add(line.toString().trim().toLowerCase());
		}
		bReader.close();
		return stopWordsList;
	}

	/**
	 * Push given json to DB
	 * 
	 * @param qJson
	 * @return
	 */
	private boolean pushToDB(JsonObject qJson) {
		// push json object to database
		Response r = this.dbClient.save(qJson);
		// return
		if (r.getError() == null)
			return true;
		else {
			System.out.println("Error: " + r.getError() + "Q: "
					+ qJson.get("description"));
			return false;
		}
	}

	/**
	 * getTags method get the list of tags from the file that are used to get
	 * the questions from stackoverflow.com
	 * 
	 * @return List of tags to get the question from
	 * @throws IOException
	 */
	List<String> getTags() throws IOException {
		List<String> tagList = new ArrayList<String>();
		String fileName = "./data/TopTags.result";
		BufferedReader breader = new BufferedReader(new FileReader(fileName));
		String tag;
		while ((tag = breader.readLine()) != null) {
			tagList.add(tag);
		}
		breader.close();
		return tagList;
	}

	/**
	 * @param args
	 * @throws ResponseException
	 * @throws NotFound
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws ResponseException, NotFound,
			IOException, InterruptedException {

		// Create GetQuestions Object
		GetQuestions getQs = new GetQuestions(210);		// Grabbing 210 questions per Tag (tag list in TopTags.result)

		// List of tags to get questions from
		List<String> tagList = getQs.getTags();

		// create view agent
		UserAgent agent = new UserAgent();

		// RegEx pattern for unique identifier of question
		// String uidPattern =
		// "(http://stackoverflow.com/questions/)(\\d+)(/.*)";

		// Iterate over the tags to get the pages
		Iterator<String> tagIterator = tagList.iterator();
		while (tagIterator.hasNext()) {
			// Construct the URL
			String url = "http://stackoverflow.com/questions/tagged/";
			// Append the URL
			url += tagIterator.next();
			System.out.println("For Tag URL: " + url);
			
			// Get Questions
			for (int page = 1; page <= getQs.totalQsPerTag / 15; ++page) {
				// Sleep for some time so that website wont block the program
				if(page % 4 == 0)
					TimeUnit.SECONDS.sleep(11);
				// URL
				String urlMain = url.toString();
				// Append the URL
				urlMain += "?page=" + page + "&sort=newest&pagesize=15";
				System.out.println("Visiting Page: " + urlMain);
				// visit the webpage
				agent.visit(urlMain);
				// webpage document
				Document webDoc = agent.doc;
				// Create Web scrapper
				WebsiteScraper webScraper;
				// Find question summary
				Elements questions = webDoc
						.findEvery("<div class=question-summary>");
				Iterator<Element> questionsIterator = questions.iterator();
				while (questionsIterator.hasNext()) {
					// Get the question block
					Element question = questionsIterator.next();
					// question linkS
					String link = question.findFirst("<a>").getAt("href")
							.toString();
					// visit the link
					agent.visit(link);

					// Send document to web scrapper to get question, question
					// description tags & code parts
					webScraper = new WebsiteScraper(agent.doc,
							getQs.getStopWords());

					// Generate a Json object to put into database
					JsonObject qJson = new JsonObject();

					/* Fill the qJson */
					// Get the unique id for the link
					// qJson.addProperty("_id", link.replaceAll(uidPattern,
					// "$2"));
					qJson.addProperty("type", "question"); // Fill the type

					// Add Question Tags
					List<String> qTags = webScraper.qTags;
					for (int t = 1; t <= qTags.size(); t++) {
						qJson.addProperty("tag" + t, qTags.get(t - 1));
					}

					/*Add Question & question description & code to qDescription variable */

					List<String> qContent = webScraper.qDescriptionTokens;
					String questionContent = "";
					for (int d = 0; d < qContent.size(); ++d) {
						questionContent += qContent.get(d) + " ";
					}
					/* Add Code Block with @suffixed and prefixed Ex: @import@ */
					List<String> qCode = webScraper.qCodeTokens;
					for (int d = 0; d < qCode.size(); ++d) {
						questionContent += "@" + qCode.get(d) + "@ ";
					}
					// TODO: may have to update all_questions view
					qJson.addProperty("qContent", questionContent);
					// Add this to DB
					boolean res = getQs.pushToDB(qJson);
					if (!res)
						System.out.println("Error!");
				}
			}
		}
	}
}
