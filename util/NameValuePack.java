package pagegen.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import pagegen.util.Enums.FormFieldEnum;
import pagegen.util.Enums.FormTypeEnum;
import pagegen.util.Enums.UrlTypeEnum;

public class NameValuePack {
	
	///////////////FORMS
	
	private String[] emailFormFieldRegexIdent = { ".*mail.*" };
	private String[] authorFormFieldRegexIdent = { ".*author.*", ".*name.*" };
	private String[] siteFormFieldRegexIdent = { ".*url.*", ".*page.*", ".*site.*", ".*web.*" };
	private String[] commentFormFieldRegexIdent = { ".*comment.*", ".*message.*", ".*text.*" };
	private String[] subjectFormFieldRegexIdent = { ".*subject.*" };
	private String[] usernameFormFieldRegexIdent = { ".*username.*" };

	private HashMap<FormFieldEnum, String[]> commentFormFieldEnumDescriptions = new HashMap<FormFieldEnum, String[]>();
	private HashMap<FormFieldEnum, String> commentFormFieldEnumValues = new HashMap<FormFieldEnum, String>();
	private HashMap<FormFieldEnum, String[]> registerFormFieldEnumDescriptions = new HashMap<FormFieldEnum, String[]>();
	private HashMap<FormFieldEnum, String> registerFormFieldEnumValues = new HashMap<FormFieldEnum, String>();

	private HashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>> formFieldNameRegex = new LinkedHashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>>();
	private HashMap<FormTypeEnum, HashMap<FormFieldEnum, String>> formFieldValues = new LinkedHashMap<FormTypeEnum, HashMap<FormFieldEnum, String>>();

	
	////////////////URLS
	
	private String[] registerUrlRegexIdent = { ".*register.*", ".*account.*" };
	private String[] loginUrlRegexIdent = { ".*login.*", ".*logged.*" };
	private String[] commentUrlRegexDescriptions = { ".*comment.*", ".*write.*" };
	private String[] emailSentDescriptions = {".*email.*sent.*"};
	
	private HashMap<UrlTypeEnum, String[]> urlRegexDescriptions = new LinkedHashMap<UrlTypeEnum, String[]>();
	
	
	
	// default US English
	public NameValuePack() {
		commentFormFieldEnumDescriptions.put(FormFieldEnum.AUTHOR,
				authorFormFieldRegexIdent);
		commentFormFieldEnumValues.put(FormFieldEnum.AUTHOR,
				"Joe Blocks");

		commentFormFieldEnumDescriptions.put(FormFieldEnum.EMAIL,
				emailFormFieldRegexIdent);
		commentFormFieldEnumValues.put(FormFieldEnum.EMAIL,
				"joe.blocks@gmail.com");

		commentFormFieldEnumDescriptions.put(FormFieldEnum.SITE,
				siteFormFieldRegexIdent);
		commentFormFieldEnumValues.put(FormFieldEnum.SITE,
				"www.google.com");

		commentFormFieldEnumDescriptions.put(FormFieldEnum.MESSAGE,
				commentFormFieldRegexIdent);
		commentFormFieldEnumValues.put(FormFieldEnum.MESSAGE,
				"I like the way you drink.");

		commentFormFieldEnumDescriptions.put(FormFieldEnum.SUBJECT,
				subjectFormFieldRegexIdent);
		commentFormFieldEnumValues.put(FormFieldEnum.SUBJECT,
				"I like the way you drink.");

		registerFormFieldEnumDescriptions.put(FormFieldEnum.USERNAME,
				usernameFormFieldRegexIdent);
		registerFormFieldEnumValues.put(FormFieldEnum.USERNAME,
				"johnnywanker");

		formFieldNameRegex.put(FormTypeEnum.MESSAGEFORM,
				commentFormFieldEnumDescriptions);
		formFieldValues.put(FormTypeEnum.MESSAGEFORM,
				commentFormFieldEnumValues);

		formFieldNameRegex.put(FormTypeEnum.REGISTRATIONFORM,
				registerFormFieldEnumDescriptions);
		formFieldValues.put(FormTypeEnum.REGISTRATIONFORM,
				registerFormFieldEnumValues);
		
		urlRegexDescriptions.put(UrlTypeEnum.LOGINURL, loginUrlRegexIdent);
		urlRegexDescriptions.put(UrlTypeEnum.REGISTERURL, registerUrlRegexIdent);
	}
	
	public String[] getCommentUrlRegexDescriptions() {
		return commentUrlRegexDescriptions;
	}
	
	public String[] getEmailSentDescriptions() {
		return emailSentDescriptions;
	}
	
	public HashMap<FormTypeEnum, HashMap<FormFieldEnum, String[]>> getFormFieldNames() {
		return formFieldNameRegex;
	}
	
	public HashMap<FormTypeEnum, HashMap<FormFieldEnum, String>> getFormFieldValues() {
		return formFieldValues;
	}
	
	
}
