package pagegen.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import pagegen.entity.PageText;
import pagegen.entity.PageUrl;
import pagegen.util.Enums.SearchEngineEnum;

public class SearchEngine {
	public static List<PageUrl> getSerpUrls(String searchString,
			SearchEngineEnum see, int serpIndex) {

		switch (see) {
		case GOOGLE:
			return Google.getGoogleSerpUrls(searchString, serpIndex);
		case YAHOO:
			return Yahoo.getYahooSerpUrls(searchString, serpIndex);
		case MSN:
			return Msn.getMsnSerpUrls(searchString, serpIndex);
		default:
			return null;
		}
	}

}

class Google {

	/**
	 * @param keyword
	 *            Keyword to search for on google.
	 * @param serpIndex
	 *            Google serp to return. First serp is identified by 1.
	 * @return List of urls from google serp.
	 */
	public static List<PageUrl> getGoogleSerpUrls(String searchString,
			int serpIndex) {

		List<PageUrl> pageUrls = new ArrayList<PageUrl>();

		String xQuery = "for $d in //a where ($d/@class = 'l' and not(ends-with($d/@href,'pdf'))) return $d/@href";

		try {

			PageText pageText = null;

			if (serpIndex == 1) {
				pageText = Http.getHttp()
						.getPageText(
								new PageUrl(
										"http://www.google.be/search?hl=en&q="
												+ java.net.URLEncoder.encode(
														searchString, "UTF-8")
												+ "&btnG=Google+Search&meta="));
			} else {
				pageText = Http.getHttp().getPageText(
						new PageUrl("http://www.google.be/search?q="
								+ java.net.URLEncoder.encode(searchString,
										"UTF-8") + "&hl=en&start="
								+ (serpIndex - 1) * 10 + "&sa=N"));
			}

			List<String> xQueryResults = Scraper.xQuery(pageText, xQuery);

			for (String xQueryResult : xQueryResults) {
				pageUrls.add(new PageUrl(xQueryResult));
			}

		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("floep.util.Google").error(
					"Unsupported encoding exception");
		} catch (Exception e) {
			Logger.getLogger("floep.util.Google").error(e);
		}

		return pageUrls;

	}
}

class Yahoo {
	public static List<PageUrl> getYahooSerpUrls(String searchString,
			int serpIndex) {

		List<PageUrl> pageUrls = new ArrayList<PageUrl>();

		String xQuery = "for $d in //a where $d/@class = 'yschttl' and not (contains($d/@href,'yahoo')) and not(ends-with($d/@href,'pdf')) return $d/@href";

		try {

			PageText pageText = null;

			if (serpIndex == 1) {
				pageText = Http
						.getHttp()
						.getPageText(
								new PageUrl(
										"http://search.yahoo.com/search?p="
												+ java.net.URLEncoder.encode(
														searchString, "UTF-8")
												+ "&y=Search&rd=r1&meta=vc%3Dbe&fr=yfp-t-501&fp_ip=BE&pstart=1&b=1"));
			} else {
				pageText = Http
						.getHttp()
						.getPageText(
								new PageUrl(
										"http://search.yahoo.com/search?p="
												+ java.net.URLEncoder.encode(
														searchString, "UTF-8")
												+ "&y=Search&rd=r1&meta=vc%3Dbe&fr=yfp-t-501&fp_ip=BE&pstart=1&b="
												+ (serpIndex - 1) * 10));
			}

			List<String> xQueryResults = Scraper.xQuery(pageText, xQuery);

			for (String xQueryResult : xQueryResults) {
				pageUrls.add(new PageUrl(xQueryResult));
			}

		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("floep.util.Google").error(
					"Unsupported encoding exception");
		} catch (Exception e) {
			Logger.getLogger("floep.util.Google").error(e);
		}

		return pageUrls;

	}
}

class Msn {
	public static List<PageUrl> getMsnSerpUrls(String searchString,
			int serpIndex) {

		List<PageUrl> pageUrls = new ArrayList<PageUrl>();

		String xQuery = "for $d in //li/h3/a where not(ends-with($d/@href,'pdf')) return $d/@href";

		try {

			PageText pageText = null;

			if (serpIndex == 1) {
				pageText = Http.getHttp().getPageText(
						new PageUrl("http://search.live.com/results.aspx?q="
								+ java.net.URLEncoder.encode(searchString,
										"UTF-8")));
			} else {
				pageText = Http.getHttp().getPageText(
						new PageUrl("http://search.live.com/results.aspx?q="
								+ java.net.URLEncoder.encode(searchString,
										"UTF-8") + "&first=" + (serpIndex - 1)
								* 10));
			}

			List<String> xQueryResults = Scraper.xQuery(pageText, xQuery);

			for (String xQueryResult : xQueryResults) {
				pageUrls.add(new PageUrl(xQueryResult));
			}

		} catch (UnsupportedEncodingException e) {
			Logger.getLogger("floep.util.Msn").error(
					"Unsupported encoding exception");
		} catch (Exception e) {
			Logger.getLogger("floep.util.Msn").error(e);
		}

		return pageUrls;
	}
}
