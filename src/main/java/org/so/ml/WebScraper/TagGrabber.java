package org.so.ml.WebScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

/**
 * TagGrabber class fetches the tags from the stackOverflow website and writes
 * them to a file. All the questions related to this tags are downloaded and
 * used for training, validation and testing. This is the start point of the
 * Project.
 *
 */
public class TagGrabber {

	public static void main(String[] args) throws ResponseException, NotFound,
			IOException {

		// Get the page
		UserAgent agent = new UserAgent();
		agent.visit("http://stackoverflow.com/tags");
		com.jaunt.Document webpage = agent.doc;
		// get the tag list
		WebsiteScraper ws = new WebsiteScraper();
		List<String> tagList = ws.getAllTags(webpage);

		// write to file
		writeTagsToFile(tagList);
	}

	private static void writeTagsToFile(List<String> tagList)
			throws IOException {
		// write to file
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./data/TopTags.result"));
		for (int t = 0, max = tagList.size(); t < max; t++) {
			bw.write(tagList.get(t).toLowerCase() + "\n");
		}
		// close
		bw.close();
	}
}
