package pagegen.util;

import java.util.ArrayList;
import java.util.List;

import pagegen.entity.Page;
import pagegen.entity.PageUrl;
import pagegen.model.Credentials;
import pagegen.model.MailInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MailUtils {
	public static MailInfo getCredentialsByMail(String domain, RegexPack regexPack)
	{
		MailInfo mailInfo = null;
		PageUrl activationUrl = null;
		String password = null;
		
		
		Logger logger = Logger.getLogger("floep.util.Email"); 
		logger.info("attempting to read e-mail message from domain " + domain);
		
		PageUrl startUrl = new PageUrl("http://www.mailinator.com");
		Page page = Http.getHttp().getPage(
				new PageUrl("http://www.mailinator.com"));

		PageUrl getUrl = new PageUrl(
				"http://www.mailinator.com/maildir.jsp?email=johnnywanker&x=24&y=21",
				startUrl);

		page = Http.getHttp().getPage(getUrl);

		List<String> senders = Scraper
				.xQuery(page.getPageText(),
						"for $d in //table[@id='inboxList']//td[not(@align) and not(text())] return $d");
		List<String> subjects = Scraper
				.xQuery(page.getPageText(),
						"for $d in //table[@id='inboxList']//td[@align='center'] return $d//a/@href");

		List<SenderSubjectPair> senderSubjectPairs = new ArrayList<SenderSubjectPair>();


		SenderSubjectPair selectedSenderSubjectPair = null;

		for (int i = 0; i < senders.size(); i++) {
			SenderSubjectPair senderSubjectPair = new SenderSubjectPair(senders
					.get(i), subjects.get(i));
			senderSubjectPairs.add(senderSubjectPair);
		}

		for (SenderSubjectPair senderSubjectPair : senderSubjectPairs) {
			if (StringUtils.containsIgnoreCase(senderSubjectPair.getSender(),
					domain)) {
				selectedSenderSubjectPair = senderSubjectPair;
				break;
			}
			
		}

		if(selectedSenderSubjectPair == null)
		{
			logger.info("No mail from " + domain);
		}
		else
		{
			mailInfo = new MailInfo();
			
			logger.info("Mail received from " + domain + "; opening mail...");
			page = Http.getHttp().getPage(
					new PageUrl(selectedSenderSubjectPair.getSubject(), startUrl));
			
			
			String activationUrlAsString = Scraper.xQueryForFirst(page.getPageText(), "for $d in //a where contains($d,'" + domain  +"') return $d/@href");
			
			if(activationUrl != null)
			{
				logger.info("Activation url found " + activationUrl);
				activationUrl = new PageUrl(activationUrlAsString);
				mailInfo.setActivationUrl(activationUrl);
				
			}
			
			password = Scraper.scrapeForFirstRegex(page.getPageText(),regexPack.getPasswordDescriptions());
			
			if(password != null)
			{
				mailInfo.setPassword(password);
			}
			
			
		}
		
	
		
		return mailInfo;
	}
	
	
}
