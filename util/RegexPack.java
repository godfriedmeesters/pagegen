package pagegen.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import pagegen.util.Enums.FormFieldEnum;
import pagegen.util.Enums.FormTypeEnum;
import pagegen.util.Enums.UrlTypeEnum;
import sun.security.action.GetLongAction;

public class RegexPack {
	private String[] emailFormFieldRegexIdent = { ".*mail.*" };
	private String[] authorFormFieldRegexIdent = { ".*author.*", ".*name.*" };
	private String[] siteFormFieldRegexIdent = { ".*url.*", ".*page.*", ".*site.*", ".*web.*" };
	private String[] commentFormFieldRegexIdent = { ".*comment.*", ".*message.*", ".*text.*" };
	private String[] subjectFormFieldRegexIdent = { ".*subject.*" };
	private String[] usernameFormFieldRegexIdent = { ".*username.*" };

	private HashMap<FormFieldEnum, String[]> commentFormFieldEnumDescriptions = new HashMap<FormFieldEnum, String[]>();
	private HashMap<FormFieldEnum, String[]> registerFormFieldEnumDescriptions = new HashMap<FormFieldEnum, String[]>();
	
	private HashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>> formFieldNameRegex = new LinkedHashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>>();
	////////////////URLS
	
	private String[] registerUrlRegexIdent = { ".*register.*", ".*account.*" };
	private String[] loginUrlRegexIdent = { ".*login.*", ".*logged.*" };
	private String[] commentUrlRegexDescriptions = { ".*comment.*", ".*write.*","*.viewtopic*.","*.post.*" };
	private String[] intermediateUrlRegexDescriptions = { ".*viewforum.*" };
	
	private String[] emailSentDescriptions = {".*email.*sent.*"};
	private String[] registeredDescriptions = {".*registered.*",".*account.*created.*"};
	private String[] logoutDescriptions = {"*.logout.*"};
	private String[] passwordDescriptions = {"(?<=Password:)(.+?)(?=([^a-z0-9]))"};
	
	private HashMap<UrlTypeEnum, String[]> urlDescriptions = new LinkedHashMap<UrlTypeEnum, String[]>();
	
	
	public RegexPack() {
		commentFormFieldEnumDescriptions.put(FormFieldEnum.AUTHOR,
				authorFormFieldRegexIdent);
	

		commentFormFieldEnumDescriptions.put(FormFieldEnum.EMAIL,
				emailFormFieldRegexIdent);
		

		commentFormFieldEnumDescriptions.put(FormFieldEnum.SITE,
				siteFormFieldRegexIdent);
		

		commentFormFieldEnumDescriptions.put(FormFieldEnum.MESSAGE,
				commentFormFieldRegexIdent);
		

		commentFormFieldEnumDescriptions.put(FormFieldEnum.SUBJECT,
				subjectFormFieldRegexIdent);
		

		registerFormFieldEnumDescriptions.put(FormFieldEnum.USERNAME,
				usernameFormFieldRegexIdent);
		

		formFieldNameRegex.put(FormTypeEnum.MESSAGEFORM,
				commentFormFieldEnumDescriptions);
		

		formFieldNameRegex.put(FormTypeEnum.REGISTRATIONFORM,
				registerFormFieldEnumDescriptions);
		
		
		urlDescriptions.put(UrlTypeEnum.LOGINURL, loginUrlRegexIdent);
		urlDescriptions.put(UrlTypeEnum.REGISTERURL, registerUrlRegexIdent);
		urlDescriptions.put(UrlTypeEnum.COMMENTURL, commentUrlRegexDescriptions);
	}
	
	
	
	public String[] getEmailSentRegex() {
		return emailSentDescriptions;
	}
	
	public String[] getLogoutDescriptions()
	{
		return logoutDescriptions;
	}
	
	public String[] getIsRegisteredRegex()
	{
		return registeredDescriptions;
	}
	
	public HashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>> getFormFieldDescriptions() {
		return formFieldNameRegex;
	}


	public HashMap<UrlTypeEnum, String[]> getUrlDescriptions() {
		return urlDescriptions;
	}



	public String[] getIntermediateUrlRegexDescriptions() {
		return intermediateUrlRegexDescriptions;
	}



	public String[] getPasswordDescriptions() {
		return passwordDescriptions;
	}
	
	
	
	
}



