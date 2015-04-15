package org.so.ml.WebScraper;

import java.util.Iterator;

import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public class Launcher {
	public static void main(String args[]) throws ResponseException, NotFound {

		UserAgent agent = new UserAgent();
		String weblink = "http://stackoverflow.com/questions/tagged/java";

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
				webScraper = new WebsiteScraper(agent.doc);
				System.out.println("****Question****");
				System.out.println(webScraper.qText);

				System.out.println("****Tags****");
				System.out.println(webScraper.qTags);

				System.out.println("****Code****");
				System.out.println(webScraper.qcodeSegments);
			}

			Element nextLink = webDoc.findFirst("<div class=\"pager fl\"");
			weblink = nextLink.findFirst("<a>").getAt("href");

		}

	}
}
