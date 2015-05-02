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
 * TagGrabber class fetches the tags from the stackOverflow website and writes
 * them to a file. All the questions related to these tags are downloaded and
 * used for training, validation and testing. This is the start point of the
 * Project.
 *
 * Gets tags from the webpage and saves to a file
 */
public class TagGrabber {

	public static void main(String[] args) throws ResponseException, NotFound,
			IOException {

		// Get the page
		UserAgent agent = new UserAgent();
		
		// Get top N tags
		List<String> tagList = new ArrayList<String>();
		int nTopTags = 152;
		for(int t=1; t<=nTopTags/33; ++t) {	// 36 tags per page but take 33 to get nearly 100 tags
			String url = "http://stackoverflow.com/tags?page=";
			url += t + "&tab=popular";
			System.out.println("url: "+ url);
			agent.visit(url);
			com.jaunt.Document webpage = agent.doc;
			// get the tag list
			WebsiteScraper ws = new WebsiteScraper();
			tagList.addAll(ws.getAllTags(webpage));
		}
		
		// write to file
		System.out.println("TagList File Generated");
		writeTagsToFile(tagList);
	}

	private static void writeTagsToFile(List<String> tagList)
			throws IOException {
		// write to file
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./data/MasterTagFile.result"));
		for (int t = 0, max = tagList.size(); t < max; t++) {
			bw.write(tagList.get(t).toLowerCase() + "\n");
		}
		// close
		bw.close();
	}
}
