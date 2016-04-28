package pagegen.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import pagegen.entity.Page;
import pagegen.entity.PageText;
import pagegen.entity.PageUrl;
import pagegen.model.Form;
import pagegen.model.FormIdentifier;

public class PageScraper extends Scraper {
	

	public static PageUrl scrapeForPageUrl(Page page, RegexPack regexPack, Enums.UrlTypeEnum urlTypeEnum) {
		List<String> urlLabels = Scraper.xQuery(page.getPageText(),
				"for $d in //a return $d");
		List<String> urlHrefs = Scraper.xQuery(page.getPageText(),
		"for $d in //a return $d/@href");

		
		String href = null;

		for (String urlLabel : urlLabels) {
			for (String urlRegexIdent: regexPack.getUrlDescriptions().get(urlTypeEnum)) {
				if (FloepUtils.matchesIgnoreCase(urlLabel,urlRegexIdent)) {
					href = xQueryForFirst(page.getPageText(),
							"for $d in //a where $d = '" + urlLabel
									+ "' return $d/@href");
					return new PageUrl(href);
				}
				break;
			}
		}
		
		if(href == null)
		{
			for (String urlHref : urlHrefs) {
				for (String urlRegexIdent: regexPack.getUrlDescriptions().get(urlTypeEnum)) {
					if (FloepUtils.matchesIgnoreCase(urlHref,urlRegexIdent)) {
						return new PageUrl(href);
					}
					
				}
			}
			
		}

		return new PageUrl(href);
	}

	public static List<Form> scrapeForForms(Page page) {
		List<Form> forms = new ArrayList<Form>();
		List<FormIdentifier> formIdentifiers = xQueryForFormIdentifiers(page.getPageText());

		for (FormIdentifier formIdentifier : formIdentifiers) {
			Form form = new Form();
			form.setFormName(formIdentifier.getName());
			form.setActionUrl(new PageUrl(formIdentifier.getAction(),page.getPageUrl()));
			form.setInputFields(scrapeForInputFields(page.getPageText(), formIdentifier));
			form.setHiddenFields(scrapeForHiddenFields(page.getPageText(), formIdentifier));
			form.setSubmitButtons(scrapeForSubmitFields(page.getPageText(), formIdentifier));
				
		}
		
		return forms;
	}

	private static List<FormIdentifier>  xQueryForFormIdentifiers(PageText pageText) {
		Set<String> xQueryResults = new LinkedHashSet<String>();
		List<FormIdentifier> formIdentifiers = new ArrayList<FormIdentifier>();

		try {

			List<String> xmlResultList = runQueryForStrings(pageText
					.getStringValue(),
					"for $d in //form return concat($d/@name,'@@',$d/@action)");

			if (xmlResultList != null && xmlResultList.size() != 0) {
				for (int i = 0; i < xmlResultList.size(); i++) {
					String ti = xmlResultList.get(i);
					xQueryResults.add(ti);
				}
			}

		} catch (Exception e) {
			Logger.getLogger("floep.util.Scraper").error(e);
		}

		for (String nvPair : xQueryResults) {
			String[] s = (" " + nvPair + " ").split("@@");

			if (s[0].matches("\\s+"))
				s[0] = "";
			if (s[1].matches("\\s+"))
				s[1] = "";

			FormIdentifier formIdentifier = new FormIdentifier();
			formIdentifier.setName(s[0].trim());
			formIdentifier.setAction(s[1].trim());

			String criteria = "";
			criteria += "@action = '"
					+ FloepUtils.convertAsciiToXml(formIdentifier
							.getAction()) + "'";

			if (formIdentifier.getName().equals("")) {
				criteria += " and not(exists(@name))";
			} else {
				criteria += " and @name = '"
						+ FloepUtils.convertAsciiToXml(formIdentifier
								.getName()) + "'";
			}

			formIdentifier.setCriteria(criteria);

			formIdentifiers.add(formIdentifier);
		}

		return formIdentifiers;
	}

	private static List<NameValuePair> scrapeForHiddenFields(PageText pageText,
			FormIdentifier formIdentifier) {
		List<NameValuePair> hiddenFieldNvps = Scraper
				.xQueryForNVPair(
						pageText,
						"for $d in //form["
								+ formIdentifier.getCriteria()
								+ "]//input where $d/@type = 'hidden' and $d/@name != '' and $d/@value != '' ");

		Logger.getLogger("floep.util.PageScraper").info(
				"Found " + hiddenFieldNvps.size() + " hidden field nvps");

		return hiddenFieldNvps;
	}

	private static List<NameValuePair> scrapeForSubmitFields(PageText pageText,
			FormIdentifier formIdentifier) {
		List<NameValuePair> submitFieldNvps = Scraper.xQueryForNVPair(pageText,
				"for $d in //form[" + formIdentifier.getCriteria()
						+ "]//input where $d/@type = 'submit' ");

		return submitFieldNvps;
	}

	private static List<NameValuePair> scrapeForInputFields(PageText pageText,
			FormIdentifier formIdentifier) {
		List<String> inputFieldNames = new ArrayList<String>();

		inputFieldNames
				.addAll(Scraper
						.xQuery(
								pageText,
								"for $d in //form["
										+ formIdentifier.getCriteria()
										+ "]//input where $d/@type != 'hidden' and $d/@type != 'submit' and $d/@type != 'checkbox' return $d/@name"));

		inputFieldNames
				.addAll(Scraper.xQuery(pageText, "for $d in //form["
						+ formIdentifier.getCriteria()
						+ "]//textarea return $d/@name"));

		List<NameValuePair> inputFields = new ArrayList<NameValuePair>();

		for (String inputFieldName : inputFieldNames) {
			inputFields.add(new NameValuePair(inputFieldName, ""));
		}

		Logger.getLogger("floep.util.PageScraper").info(
				"Found " + inputFields.size() + " input/textarea fields");

		return inputFields;
	}

}
