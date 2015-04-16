package org.so.ml.WebScraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public class Launcher {
	/**
	 * getStopWords method gets the English stop words from a file and returns
	 * them as a list
	 * 
	 * @return
	 * @throws IOException
	 */
	static List<String> getStopWords() throws IOException {
		List<String> stopWordsList = new ArrayList<String>();
		String stopWordsFile = "./data/StopWords.txt";
		BufferedReader breader = new BufferedReader(new FileReader(
				stopWordsFile));
		String line;
		while ((line = breader.readLine()) != null) {
			stopWordsList.add(line.toString().trim());
		}
		breader.close();
		return stopWordsList;
	}
	
	public static void main(String args[]) throws ResponseException, NotFound, IOException {

		UserAgent agent = new UserAgent();
		String weblink = "http://stackoverflow.com/questions/tagged/python";

		/* loop through the pages each having 15 questions */
		for (int pageno = 1; pageno < 2; pageno++) {
			agent.visit(weblink);
			Document webDoc = agent.doc;
			WebsiteScraper webScraper;
			Elements questions = webDoc
					.findEvery("<div class=question-summary>");
			Iterator<Element> questionsIterator = questions.iterator();
			while (questionsIterator.hasNext()) {
				Element question = questionsIterator.next();
				String link = question.findFirst("<a>").getAt("href")
						.toString();
				agent.visit(link);
				webScraper = new WebsiteScraper(agent.doc, getStopWords());
				System.out.println("****Question Description Tokens****");
				System.out.println(webScraper.qDescriptionTokens);

				System.out.println("****Tags****");
				System.out.println(webScraper.qTags);

				System.out.println("\n\n****Code Tokens****");
				System.out.println(webScraper.qCodeTokens);
			}

			Element nextLink = webDoc.findFirst("<div class=\"pager fl\"");
			weblink = nextLink.findFirst("<a>").getAt("href");

		}

	}
}
