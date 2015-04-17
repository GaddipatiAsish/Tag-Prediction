package org.so.ml.WebScraper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;

public class WebsiteScraper {

	/* list that stores the tags of the question */
	protected List<String> qTags = new ArrayList<String>();

	/* List of code fragments in the question */
	protected List<String> qCodeSegments = new ArrayList<String>();
	/* List of Tokens of the Question Code Segments */
	List<String> qCodeTokens = new ArrayList<String>();

	/* Description of the question */
	protected String qDescription = new String();
	/* List of Tokens of the Question Description */
	List<String> qDescriptionTokens = new ArrayList<String>();

	/**
	 * WebsiteScraper method takes the document of website as input and parses
	 * it into question, code and tags
	 * 
	 * @param website
	 * @throws NotFound
	 */
	public WebsiteScraper(Document website, List<String> stopWordsList)
			throws NotFound {
		/* get the postcell tag where the question resides. */
		Elements postcell = website.findEvery("<td class=postcell");
		/* traverse through the postcell tags to get the content of the Question */
		Element div = postcell.findFirst("<div>");
		Elements contents = div.findEvery("<div>");
		Iterator<Element> iterator = contents.iterator();
		while (iterator.hasNext()) {
			/* Get the Question */
			Element keyword = iterator.next();
			if (keyword.getAt("class").compareTo("post-text") == 0
					&& keyword.getAt("itemprop").compareTo("text") == 0) {
				separateTextAndCode(keyword, stopWordsList);
			}
			/* Get the tags for question */
			if (keyword.getAt("class").compareTo("post-taglist") == 0) {
				Elements tags = keyword.findEvery("<a>");
				Iterator<Element> tagIterator = tags.iterator();
				while (tagIterator.hasNext()) {
					qTags.add(tagIterator.next().getText().toString());
				}
			}
		}
	}

	/**
	 * seperateTextAndCode method separates the text of the question and the
	 * code in the question
	 * 
	 * @throws NotFound
	 */
	public void separateTextAndCode(Element question, List<String> stopWordsList)
			throws NotFound {
		/* Get the Code in the Question */
		Elements codeElements = question.findEvery("<pre>");
		Iterator<Element> codeIterator = codeElements.iterator();
		while (codeIterator.hasNext()) {
			qCodeSegments.add(codeIterator.next().getFirst("<code>")
					.innerText());
		}

		/* Get the text in the Question */
		Elements textElements = question.findEvery("<p>");
		Iterator<Element> textIterator = textElements.iterator();
		while (textIterator.hasNext()) {
			qDescription += textIterator.next().innerText();
		}

		/* Tokenize the code */
		qCodeTokenizer(qCodeSegments, stopWordsList);
		/* Tokenize the description of the question */
		qDescriptionTokenizer(qDescription, stopWordsList);

	}

	/**
	 * qDescriptionTokenizer method tokenizes the description of the question
	 * and removes the stop words from the tokens
	 * 
	 * @param qtext
	 */
	void qDescriptionTokenizer(String qDescription, List<String> stopWordsList) {
		/* replace the special characters with a space */
		qDescription = qDescription.replaceAll("[^a-zA-Z0-9]", " ");
		/* tokenize and add tokens to list tokens */
		StringTokenizer tokenizer = new StringTokenizer(qDescription, " ");
		while (tokenizer.hasMoreTokens()) {
			String tok = tokenizer.nextToken();
			String currentToken = tok.trim();
			if (currentToken.length() > 1) {
				qDescriptionTokens.add(currentToken);
			}
		}
		/* remove the stop words */
		qDescriptionTokens.removeAll(stopWordsList);
	}

	/**
	 * qCodeTokenizer method tokenizes the code of the question and removes the
	 * stop words from the tokens
	 * 
	 * @param codeSegments
	 * @param stopWordsList
	 */
	void qCodeTokenizer(List<String> codeSegments, List<String> stopWordsList) {
		Iterator<String> iterator = codeSegments.iterator();
		/* loop through the code segments */
		while (iterator.hasNext()) {
			String codeBlock = iterator.next();
			/* get the alphanumeric's from code Segments */
			codeBlock = codeBlock.replaceAll("[^a-zA-Z]", " ");
			/* tokenize the code block and add them to list qCodeTokens */
			StringTokenizer tokenizer = new StringTokenizer(codeBlock, " ");
			while (tokenizer.hasMoreTokens()) {
				String tok = tokenizer.nextToken();
				String currentToken = tok.trim();
				if (currentToken.length() > 1) {
					qCodeTokens.add(currentToken);
				}
			}
		}
		/* remove the stop words */
		qCodeTokens.removeAll(stopWordsList);
	}



}
