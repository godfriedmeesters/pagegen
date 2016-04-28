package pagegen.util;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import pagegen.entity.Page;
import pagegen.entity.PageUrl;
import pagegen.model.Credentials;
import pagegen.model.Form;
import pagegen.model.MailInfo;
import pagegen.util.Enums.FormFieldEnum;
import pagegen.util.Enums.FormTypeEnum;
import pagegen.util.Enums.UrlTypeEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TestProm extends TestCase {

	public void testProm() throws IOException {
		boolean isCommentAdded = false;
		boolean isRegistered = false;
		boolean isLoggedOn = false;
		boolean giveUp = false;
		String username = null;
		String password = null;
		Form messageForm = null;

		RegexPack regexPack = new RegexPack();
		ValuePack valuePack = new ValuePack();

		PageUrl pageUrl = new PageUrl("http://allbeautifulwoman.com");
		Logger logger = Logger.getLogger("floep.prom");
		logger.info("Attempting to comment on url " + pageUrl);

		while (!isCommentAdded && !giveUp) {
			Page page = Http.getHttp().getPage(pageUrl);
			List<Form> forms = PageScraper.scrapeForForms(page);

			logger.info("Found " + forms.size() + " forms on " + pageUrl);

			for (Form form : forms) {
				logger.info("Current form name is '" + form.getFormName()
						+ "' and action url is " + form.getActionUrl());
				form.setFormType(FormUtils.determineFormType(form, regexPack));
				logger.info("Current form type is " + form.getFormType());

				
				if(form.getFormType().equals(FormTypeEnum.MESSAGEFORM))
					messageForm = form;
					
				if (messageForm != null) {
					FormUtils.fillForm(messageForm, regexPack, valuePack);
					page = Http.getHttp().postForm(messageForm);
					if (FloepUtils.containsIgnoreCase(page.getPageText(),
							valuePack.getFormFieldValue(
									FormTypeEnum.MESSAGEFORM,
									FormFieldEnum.MESSAGE))) {
						isCommentAdded = true;
						logger.info("Comment '"
								+ StringUtils.left(valuePack.getFormFieldValue(
										FormTypeEnum.MESSAGEFORM,
										FormFieldEnum.MESSAGE), 15) + "..."
								+ "' has been added successfully.");
						break;
					}
				}

				if (form.getFormType().equals(FormTypeEnum.REGISTRATIONFORM)) {
					if (!isRegistered) {
						logger
								.info("No account registered yet, attempting to register account with username '"
										+ valuePack.getFormFieldValue(
												FormTypeEnum.REGISTRATIONFORM,
												FormFieldEnum.USERNAME)
										+ "' and password '"
										+ valuePack.getFormFieldValue(
												FormTypeEnum.REGISTRATIONFORM,
												FormFieldEnum.PASSWORD) + "'");

						FormUtils.fillForm(form, regexPack, valuePack);
						page = Http.getHttp().postForm(form);

						if (FloepUtils.matchesIgnoreCase(page.getPageText(),
								regexPack.getIsRegisteredRegex())) {

							if (FloepUtils.matches(page.getPageText(),
									regexPack.getEmailSentRegex())) {
								logger
										.info("Verification email has been sent by '"
												+ FloepUtils
														.extractHost(pageUrl)
												+ "'");
								MailInfo mailInfo = MailUtils
										.getCredentialsByMail(FloepUtils
												.extractHost(pageUrl),
												regexPack);

								if (mailInfo != null) {
									if (mailInfo.getActivationUrl() != null) {
										logger
												.info("Activation url retrieved: "
														+ mailInfo
																.getActivationUrl());
										page = Http.getHttp().getPage(
												mailInfo.getActivationUrl());
									}

									username = valuePack.getFormFieldValue(
											FormTypeEnum.REGISTRATIONFORM,
											FormFieldEnum.USERNAME);

									if (mailInfo.getPassword() != null) {
										password = mailInfo.getPassword();

									} else {
										password = valuePack.getFormFieldValue(
												FormTypeEnum.REGISTRATIONFORM,
												FormFieldEnum.PASSWORD);
									}

								}

							} else {
								logger
										.info("Registration successful; e-mail activation was not necessary.");
								isRegistered = true;
							}

						} else {
							logger
									.info("We're already registered, not registering again!");
						}
					}
				}

				if (form.getFormType().equals(FormTypeEnum.LOGINFORM)) {
					if (!isLoggedOn) {
						if (isRegistered) {
							logger.info("Attempting to login with username "
									+ username + " and password " + password);
							valuePack.setFormValue(FormTypeEnum.LOGINFORM,
									FormFieldEnum.USERNAME, username);
							valuePack.setFormValue(FormTypeEnum.LOGINFORM,
									FormFieldEnum.PASSWORD, password);
							FormUtils.fillForm(form, regexPack, valuePack);
							page = Http.getHttp().postForm(form);

							if (FloepUtils.matches(page.getPageText(),
									regexPack.getLogoutDescriptions())) {
								isLoggedOn = true;
								logger
										.info("logged in successfully with username "
												+ username
												+ " and password "
												+ password);
							}
						} else {
							logger
									.info("Attempting to login, but not registered yet");
						}
					} else {
						logger
								.info("We're already logged in, not logging in again");
					}
				}
			}

			int crawlDepth = 0;
			boolean postingUrlFound = false;
			boolean intermediateUrlFound = false;

			do {
				if (!isCommentAdded && !giveUp) {
					pageUrl = PageScraper.scrapeForPageUrl(page, regexPack,
							UrlTypeEnum.COMMENTURL);

					if (pageUrl == null) {
						logger
								.info("No posting url found, looking for intermediate url...");
						pageUrl = PageScraper.scrapeForPageUrl(page, regexPack,
								UrlTypeEnum.INTERMEDIATEURL);

						if (pageUrl == null) {
							logger
									.info("No intermediate url found either, giving up...");
							giveUp = true;
							break;
						} else {
							logger.info("Intermediate url found " + pageUrl);
							crawlDepth++;
							intermediateUrlFound = true;
							page = Http.getHttp().getPage(pageUrl);
						}
					} else {
						logger.info("Moving on to comment url " + pageUrl);
						postingUrlFound = true;
						crawlDepth++;
					}
				}

			} while (intermediateUrlFound && crawlDepth < 3 && !postingUrlFound);
		}
	}
}

// nl/ok "http://gerrie.wordpress.com/2007/04/04/hello-world/#comments"
// nl/ok
// "http://www.pbase.com/fohoizey/lille_metropolis&gcmd=add_comment"
// nl/ok
// "http://www.jamaica-gleaner.com/gleaner/weeklypoll/answer_listing.php?offset=196&pid=168"
// nl/ok "http://xdianalynn.wordpress.com/"

// l/nok
// "http://www.hrdownloads.com/index.php?option=com_simpleboard&Itemid=8&func=post&do=quote&replyto=19&catid=2"
// l/nok "http://lovelyyyy.skyrock.com/"
// l/nok "http://www.thegroop.net/wordpress/?m=200509"
// l/nok "http://mm.cpluv.com/joannekok/media/261340#comments"
// l/nok "http://allbeautifulwoman.com/"

// l/nok supreme "http://www.bbc.co.uk/dna/606/A27627078"
// l/nok beginner "http://www.fannation.com/throwdowns/show/30833"

// nog prob:
// http://www.volcanoetna.com/blog/2007/10/05/wine-tourism-sicily/#more-83

// ////////////////////////////
