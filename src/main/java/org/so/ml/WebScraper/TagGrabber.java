package org.so.ml.WebScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

/**
<<<<<<< HEAD
 * TagGrabber class fetches the tags from the stackOverflow website and writes
 * them to a file. All the questions related to this tags are downloaded and
 * used for training, validation and testing. This is the start point of the
 * Project.
 *
=======
 * Gets tags from the webpage and saves to a file
 * @author chinmaya
>>>>>>> eb36a73d50563b8fe29868fd8a2f049631cd1ffd
 */
public class TagGrabber {

	public static void main(String[] args) throws ResponseException, NotFound,
			IOException {

		// Get the page
		UserAgent agent = new UserAgent();
<<<<<<< HEAD
		agent.visit("http://stackoverflow.com/tags");
		com.jaunt.Document webpage = agent.doc;
		// get the tag list
		WebsiteScraper ws = new WebsiteScraper();
		List<String> tagList = ws.getAllTags(webpage);

=======
		String url = "http://stackoverflow.com/tags?page=";
		
		// Get top N tags
		List<String> tagList = new ArrayList<String>();
		int nTopTags = 100;
		for(int t=0; t<nTopTags/33; ++t) {	// 36 tags per page but take 33 to get nearly 100 tags
			url += t + "&tab=popular";
			agent.visit(url);
			com.jaunt.Document webpage = agent.doc;
			// get the tag list
			WebsiteScraper ws = new WebsiteScraper();
			tagList.addAll(ws.getAllTags(webpage));
		}
		
>>>>>>> eb36a73d50563b8fe29868fd8a2f049631cd1ffd
		// write to file
		System.out.println("TagList File Generated");
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
