package pagegen.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import pagegen.util.Enums.FormFieldEnum;
import pagegen.util.Enums.FormTypeEnum;

public class ValuePack {
///////////////FORMS
	
	private HashMap<FormFieldEnum, String> commentFormFieldEnumValues = new HashMap<FormFieldEnum, String>();
	
	private HashMap<FormFieldEnum, String> registerFormFieldEnumValues = new HashMap<FormFieldEnum, String>();

	private HashMap<FormTypeEnum, HashMap<FormFieldEnum, String>> formFieldValues = new LinkedHashMap<FormTypeEnum, HashMap<FormFieldEnum, String>>();

	
	////////////////URLS
	
	
	
	
	// default US English
	public ValuePack() {
		
		commentFormFieldEnumValues.put(FormFieldEnum.AUTHOR,
				"Joe Blocks");

		
		commentFormFieldEnumValues.put(FormFieldEnum.EMAIL,
				"joe.blocks@gmail.com");

		
		commentFormFieldEnumValues.put(FormFieldEnum.SITE,
				"www.google.com");

		
		commentFormFieldEnumValues.put(FormFieldEnum.MESSAGE,
				"I like the way you drink.");

	
		commentFormFieldEnumValues.put(FormFieldEnum.SUBJECT,
				"I like the way you drink.");

		
		registerFormFieldEnumValues.put(FormFieldEnum.USERNAME,
				"johnnywanker");
		
		registerFormFieldEnumValues.put(FormFieldEnum.PASSWORD,
		"696969");

		
		formFieldValues.put(FormTypeEnum.MESSAGEFORM,
				commentFormFieldEnumValues);

		
		formFieldValues.put(FormTypeEnum.REGISTRATIONFORM,
				registerFormFieldEnumValues);
		
		
	}
	
	public void setFormValue(FormTypeEnum formTypeEnum, FormFieldEnum formFieldEnum, String value )
	{
		formFieldValues.get(formTypeEnum).put(formFieldEnum, value);
	}
	
	public HashMap<FormTypeEnum, HashMap<FormFieldEnum, String>> getFormFieldValues() {
		return formFieldValues;
	}
	
	public String getFormFieldValue(FormTypeEnum formTypeEnum, FormFieldEnum formFieldEnum)
	{
		return formFieldValues.get(formTypeEnum).get(formFieldEnum);
	}
}

