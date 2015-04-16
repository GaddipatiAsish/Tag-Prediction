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
	protected List<String> qcodeSegments = new ArrayList<String>();
	/* text of the question */
	protected String qText = new String();

	/**
	 * takes the document of website as input and parses it into question, code
	 * and tags
	 * 
	 * @param website
	 * @throws NotFound
	 */
	public WebsiteScraper(Document website) throws NotFound {
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
				separateTextAndCode(keyword);
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
	public void separateTextAndCode(Element question) throws NotFound {
		/* Get the Code in the Question */
		Elements codeElements = question.findEvery("<pre>");
		Iterator<Element> codeIterator = codeElements.iterator();
		while (codeIterator.hasNext()) {
			qcodeSegments.add(codeIterator.next().getFirst("<code>")
					.innerText());
		}
		/* Get the text in the Question */
		Elements textElements = question.findEvery("<p>");
		Iterator<Element> textIterator = textElements.iterator();
		while (textIterator.hasNext()) {
			qText += textIterator.next().innerText();
		}
		codeTokenizer(qcodeSegments);
	}

	/**
	 * 
	 * @param qtext
	 */
	/* List<String> */void textTokenizer(String qtext) {
		List<String> tokens = new ArrayList<String>();
		/* replace the speacial characters with a space */

		System.out.println("**modified**");
		System.out.println(qtext.replaceAll("[^a-zA-Z0-9]", " "));

		// return tokens;
	}

	void codeTokenizer(List<String> codeSegments) {
		Iterator<String> iterator = codeSegments.iterator();
		while (iterator.hasNext()) {
			String codeBlock = iterator.next();
			/* get the alphanumeric's from code Segments */
			codeBlock = codeBlock.replaceAll("[^a-zA-Z]", " ");
			StringTokenizer tokenizer = new StringTokenizer(codeBlock, " ");
			while (tokenizer.hasMoreTokens()) {
				String tok = tokenizer.nextToken();
				if (tok.trim().length() > 1) {
					System.out.print(tok + ",");
				}
			}
		}
	}
}
