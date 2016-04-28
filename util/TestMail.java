package pagegen.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.query.XQueryExpression;
import pagegen.entity.Page;
import pagegen.entity.PageUrl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class TestMail extends TestCase {
	public void testMail() throws IOException {

		String host = "vivant";

		PageUrl startUrl = new PageUrl("http://www.mailinator.com");
		Page page = Http.getHttp().getPage(
				new PageUrl("http://www.mailinator.com"));

		PageUrl getUrl = new PageUrl(
				"http://www.mailinator.com/maildir.jsp?email=jimmyr&x=24&y=21",
				startUrl);

		page = Http.getHttp().getPage(getUrl);

		// FileUtils.writeStringToFile(new File("c:\\crap\\test.html"),
		// page.getPageText().toString());

		List<String> senders = Scraper
				.xQuery(page.getPageText(),
						"for $d in //table[@id='inboxList']//td[not(@align) and not(text())] return $d");
		List<String> subjects = Scraper
				.xQuery(page.getPageText(),
						"for $d in //table[@id='inboxList']//td[@align='center'] return $d//a/@href");

		List<SenderSubjectPair> senderSubjectPairs = new ArrayList<SenderSubjectPair>();

		for (String sender : senders) {
			System.out.println(sender);
		}

		for (String subject : subjects) {
			System.out.println(subject);
		}

		SenderSubjectPair selectedSenderSubjectPair = null;

		for (int i = 0; i < senders.size(); i++) {
			SenderSubjectPair senderSubjectPair = new SenderSubjectPair(senders
					.get(i), subjects.get(i));
			senderSubjectPairs.add(senderSubjectPair);
		}

		for (SenderSubjectPair senderSubjectPair : senderSubjectPairs) {
			if (StringUtils.containsIgnoreCase(senderSubjectPair.getSender(),
					host)) {
				selectedSenderSubjectPair = senderSubjectPair;
				break;
			}
			
		}

		page = Http.getHttp().getPage(
				new PageUrl(selectedSenderSubjectPair.getSubject(), startUrl));
		
		
		
		String pageUrl = Scraper.xQueryForFirst(page.getPageText(), "for $d in //a where contains($d,'" + host  +"') return $d/@href");
		System.out.println(pageUrl);
		
		
		
		
		String password = Scraper.scrapeForFirstRegex(page.getPageText().toString(),"(?<=Password:)(.+?)(?=([^a-z0-9]))").trim();
		
		
		System.out.println(password);
		FileUtils.writeStringToFile(new File("c:\\crap\\test.html"), page
				.getPageText().toString());

	}
}

class SenderSubjectPair {
	private String sender;
	private String subject;

	public SenderSubjectPair(String sender, String message) {
		this.sender = sender;
		this.subject = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSubject() {
		return subject;
	}

	public void setMessage(String message) {
		this.subject = message;
	}
}
