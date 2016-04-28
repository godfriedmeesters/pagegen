package pagegen.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.tinytree.TinyNodeImpl;
import pagegen.entity.ContentGenPage;
import pagegen.entity.Page;
import pagegen.entity.PageText;
import pagegen.entity.PageUrl;
import pagegen.entity.Proxy;
import pagegen.entity.Sentence;
import pagegen.model.FormIdentifier;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Utility class to scrape a given input text for xQuery expressions, without
 * connecting to the internet.
 * 
 * @author gmeester
 * 
 */
/**
 * @author Floep
 * 
 */
public class Scraper {

	public static List<Proxy> scrapeForProxies(PageText pageText) {
		List<String> stringResults = Scraper.scrapeForRegex(
				"\\w+\\.\\w+\\.\\w+\\.\\w+:\\d+", pageText.getStringValue());

		List<Proxy> proxyResults = new ArrayList<Proxy>();

		for (String s : stringResults) {
			String[] proxyAndPort = s.split(":");
			Proxy proxy = new Proxy(proxyAndPort[0], Integer
					.valueOf(proxyAndPort[1]));
			proxyResults.add(proxy);
		}

		return proxyResults;
	}

	public static List<String> scrapeForRegex(String regex, String sourceText) {
		Pattern pattern = Pattern.compile(regex);
		List<String> results = new ArrayList<String>();

		Matcher matcher = pattern.matcher(sourceText);

		while (matcher.find()) {
			results.add(matcher.group());
		}

		return results;
	}

	public static String scrapeForFirstRegex(PageText pageText,String[] regex)
	{
		String result = null;
		for(String oneRegex : regex)
		{
			result = scrapeForFirstRegex(pageText.toString(), oneRegex);
			if(result != null)
				return result.trim();
		}
		
		return null;
	}
	
	public static String scrapeForFirstRegex(String sourceText, String regex) {
		List<String> results = scrapeForRegex(regex, sourceText);
		if (results.size() > 0)
			return scrapeForRegex(regex, sourceText).get(0);
		else
			return "";
	}

	public static List<Sentence> scrapeForSentences(PageText pageText) {

		List<Sentence> sentences = new ArrayList<Sentence>();

		String sourceText = pageText.getStringValue();

		sourceText = sourceText
				.replaceAll(
						"</?((a|A|i|I|b|B|tt|TT|sup|SUP|strong|STRONG|span|SPAN|font|FONT).*?)>",
						" ");
		sourceText = sourceText.replaceAll("<!--.+?-->", " ");
		sourceText = sourceText.replaceAll("<script.+?script>", " ");
		sourceText = sourceText.replaceAll("\\s+", " ");

		Pattern pattern = Pattern
				.compile("(?<!(([a-z0-9]\\s)|[A-Z/(-,;0-9]|\"))[A-Z]((\\w\\.\\w\\.)|(\\d+\\.\\d+)|(vs\\.)|\\w|\\t|\\s||\\u00A0|/(?!(/))|[,\"\'()%:-]|\\;|€|\\$|£){40,500}[.!?](?!(\\d|\"|[A-Za-z]|\\.|(\\s[a-z])))");

		Matcher matcher = pattern.matcher(sourceText);

		while (matcher.find()) {

			String line = matcher.group().trim();

			Pattern pattern2 = Pattern.compile("[ ][A-Z]\\w+");
			Matcher matcher2 = pattern2.matcher(line);
			int majorWords = 0;
			while (matcher2.find())
				majorWords++;

			Pattern pattern3 = Pattern.compile("[ ][a-z]\\w+");
			Matcher matcher3 = pattern3.matcher(line);
			int minorWords = 0;
			while (matcher3.find())
				minorWords++;

			int leftOnes = 0, rightOnes = 0, quoteCount = 0;

			for (char c : line.toCharArray())

			{

				if (c == '{' || c == '(')

					leftOnes++;

				if (c == '}' || c == ')')

					rightOnes++;

				if (c == '\"')

					quoteCount++;

			}

			if (!line.matches(".*[BCDEFGHJKLMNOPQRSTUVWXYZ]\\s+[A-Z].*")
					&& (leftOnes == rightOnes) && (quoteCount % 2 == 0)
					&& (minorWords > (majorWords / 2)) && line.length() > 40)
				sentences.add(new Sentence(line));

		}

		return sentences;

	}

	public static List<ContentGenPage> scrapeForEmptyChildPages(
			ContentGenPage page) {
		List<PageUrl> pageUrls = scrapePageForUrls(page);
		List<ContentGenPage> childPages = new ArrayList<ContentGenPage>();

		for (PageUrl pageUrl : pageUrls) {
			ContentGenPage childPage = new ContentGenPage(pageUrl, page);
			childPages.add(childPage);
		}
		return childPages;
	}


	public static List<PageUrl> scrapePageForUrls(Page page) {
		List<PageUrl> originalDerivedUrls = scrapeForUntransformedUrls(page
				.getPageText());
		List<PageUrl> transformedDerivedUrls = new ArrayList<PageUrl>();

		for (PageUrl originalDerivedUrl : originalDerivedUrls) {
			transformedDerivedUrls.add(new PageUrl(originalDerivedUrl
					.getStringValue(), page.getPageUrl()));
		}

		return transformedDerivedUrls;
	}

	/**
	 * @param pageText
	 *            Text to srape from.
	 * @return List of all the urls in pageText.
	 * 
	 */
	public static List<PageUrl> scrapeForUntransformedUrls(PageText pageText) {

		String query = "for $d in //a/@href where (not(ends-with($d,'pdf')) and not(ends-with($d,'mp3'))"
				+ "and not(ends-with($d,'doc')) and not(contains($d, 'javascript')) "
				+ "and not (contains($d, 'mailto'))) return $d";

		List<String> stringUrlList = xQuery(pageText, query);

		Set<String> set = new LinkedHashSet<String>();
		set.addAll(stringUrlList);

		List<PageUrl> pageUrlList = new ArrayList<PageUrl>();

		for (String s : set) {
			pageUrlList.add(new PageUrl(s));
		}

		return pageUrlList;
	}

	public static String xQueryForFirst(PageText pageText, String query) {
		List<String> xQueryResults = xQuery(pageText, query);

		if (xQueryResults != null && xQueryResults.size() != 0) {
			return xQueryResults.get(0);
		} else
			return "";
	}
		
	public static String xQueryForFirstHref(PageText pageText, String[] targets)
	{
		for(String target: targets)
		{
			String firstResult = xQueryForFirst(pageText, "for $d in //a where contains(upper-case($d),upper-case('@@')) return $d/@href".replace("@@", target));
			if(firstResult != "")
				return firstResult;
		}
		
		return "";
	}

	
	

	/**
	 * @param sourceText
	 * @param queryWithoutReturn
	 * @return name = "[NoName]" if name not defined; value = "" if value not
	 *         defined
	 */
	public static List<NameValuePair> xQueryForNVPair(PageText sourceText,
			String queryWithoutReturn) {
		Set<String> xQueryResults = new LinkedHashSet<String>();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {

			List<String> xmlResultList = runQueryForStrings(sourceText
					.getStringValue(), queryWithoutReturn + " return concat('@',$d/@name,'@:@',$d/@value,'@')");

			if (xmlResultList != null && xmlResultList.size() != 0) {
				for (int i = 0; i < xmlResultList.size(); i++) {
					String ti = xmlResultList.get(i);
					xQueryResults.add(ti);
				}
			} else {
				Logger.getLogger("floep.util.Scraper").info(
						"Query \"" + queryWithoutReturn + "\" didn't return any results");
			}

		} catch (Exception e) {
			Logger.getLogger("floep.util.Scraper").error(e);
		}

		for (String nvPair : xQueryResults) {
			String[] s = (" " + nvPair + " ").split(":");
		
				
			s[0] = s[0].trim();
			s[0] = StringUtils.removeStart(s[0], "@");
			s[0] = StringUtils.removeEnd(s[0], "@");
			
			s[1] = s[1].trim();
			s[1] = StringUtils.removeStart(s[1], "@");
			s[1] = StringUtils.removeEnd(s[1], "@");
		
			if (s[0].matches("\\s*"))
				s[0] = "";
			if (s[1].matches("\\s+"))
				s[1] = "";
		
			
			
			NameValuePair nameValuePair = new NameValuePair(s[0], s[1]);
			nameValuePairs.add(nameValuePair);
		}

		return nameValuePairs;
	}

	/**
	 * @param pageText
	 *            The text to scrape from.
	 * @param query
	 *            Any xQuery expression.
	 * @return List of all results retrieved by query.
	 */

	@SuppressWarnings("unchecked")
	public static List<String> xQuery(PageText sourceText, String query) {

		Set<String> xQueryResults = new LinkedHashSet<String>();

		try {

			List<TinyNodeImpl> xmlResultList = runQuery(sourceText
					.getStringValue(), query);

			if (xmlResultList != null && xmlResultList.size() != 0) {
				for (int i = 0; i < xmlResultList.size(); i++) {
					TinyNodeImpl ti = xmlResultList.get(i);
					xQueryResults.add(ti.getStringValue());
				}
			} else {
				Logger.getLogger("floep.util.Scraper").info(
						"Query \"" + query + "\" didn't return any results");
			}

		} catch (Exception e) {
			Logger.getLogger("floep.util.Scraper").error(e);
		}

		return new ArrayList<String>(xQueryResults);
	}

	@SuppressWarnings("unchecked")
	public static List<String> runQueryForStrings(String pageText, String query)
			throws NullPointerException {
		List<String> results = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(pageText
				.getBytes());

		try {

			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(reader, new InputSource(bais)),
					result);

			Configuration c = new Configuration();
			StaticQueryContext qp = new StaticQueryContext(c);

			XQueryExpression xe = qp.compileQuery(query);
			DynamicQueryContext dqc = new DynamicQueryContext(c);
			dqc
					.setContextItem(c.buildDocument(new DOMSource(result
							.getNode())));
			results = xe.evaluate(dqc);
		} catch (Exception e) {
			Logger.getLogger("floep.util.Scraper").warn(e);
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	public static List<TinyNodeImpl> runQuery(String pageText, String query)
			throws NullPointerException {
		List<TinyNodeImpl> results = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(pageText
				.getBytes());

		try {

			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(reader, new InputSource(bais)),
					result);

			Configuration c = new Configuration();
			StaticQueryContext qp = new StaticQueryContext(c);

			XQueryExpression xe = qp.compileQuery(query);
			DynamicQueryContext dqc = new DynamicQueryContext(c);
			dqc
					.setContextItem(c.buildDocument(new DOMSource(result
							.getNode())));
			results = xe.evaluate(dqc);
		} catch (Exception e) {
			Logger.getLogger("floep.util.Scraper").warn(e);
		}

		return results;
	}

	public static String scrapeForTitle(PageText pageText) {
		Pattern pattern = Pattern.compile("(?<=(<title>)).+(?=(</title>))",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(pageText.getStringValue());

		if (matcher.find()) {
			if (matcher.group().trim().length() > 255)
				return matcher.group().trim().substring(0, 252) + "...";
			else
				return matcher.group().trim();
		} else {
			return "";
		}
	}

	public static String scrapeLeft(String source, int lenght) {
		if (source.length() > lenght) {
			String previous = source;

			while (source.length() > lenght) {
				source = source.replaceAll("\\b([^ ])+?\\s?$", "");
				source = source.replaceAll("(\\s|[\"(-)!?,--])+$", "");

				if (previous.equals(source))
					break;
				else
					previous = source;

			}

			source += "...";
		}

		return source;
	}

	public static String scrapeForForm(PageText pageText) {
		return null;
	}
}
