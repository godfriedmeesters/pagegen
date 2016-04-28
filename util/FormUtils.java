package pagegen.util;

import java.util.HashMap;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import pagegen.model.Form;
import pagegen.util.FloepUtils;
import pagegen.util.Enums.FormTypeEnum;

public class FormUtils {

	public static void fillForm(Form form, RegexPack regexPack, ValuePack valuePack

	) {
		int filledFields = 0;
		
		HashMap<Enums.FormFieldEnum, String[]> formSpecificFieldDescriptions = regexPack.getFormFieldDescriptions().get(form.getFormTypeEnum());
		HashMap<Enums.FormFieldEnum, String> formSpecificFieldValues = valuePack.getFormFieldValues().get(form.getFormTypeEnum());

		for (NameValuePair FormFieldEnumNvp : form.getInputFields()) {

			for (Enums.FormFieldEnum FormFieldEnum : formSpecificFieldDescriptions.keySet())

				for (String FormFieldEnumDescription : formSpecificFieldDescriptions
						.get(FormFieldEnum)) {

					if (FloepUtils.matchesIgnoreCase(
							FormFieldEnumNvp.getName(), FormFieldEnumDescription)) {
						FormFieldEnumNvp.setValue(formSpecificFieldValues.get(FormFieldEnum));
						filledFields++;
						break;
					}

				}
		}
		
		Logger.getLogger("floep.util.FormUtils").info("Form filled with " + filledFields + " values.");
	}
	
	
	public void fillLoginForm(Form form, RegexPack regexPack, ValuePack valuePack)
	{
		int filledFields = 0;
		HashMap<Enums.FormFieldEnum, String[]> formSpecificFieldDescriptions = regexPack.getFormFieldDescriptions().get(Enums.FormTypeEnum.LOGINFORM);
		HashMap<Enums.FormFieldEnum, String> formSpecificFieldValues = valuePack.getFormFieldValues().get(Enums.FormTypeEnum.LOGINFORM);

		for (NameValuePair FormFieldEnumNvp : form.getInputFields()) {

			for (Enums.FormFieldEnum FormFieldEnum : formSpecificFieldDescriptions.keySet())

				for (String FormFieldEnumDescription : formSpecificFieldDescriptions
						.get(FormFieldEnum)) {

					if (FloepUtils.matchesIgnoreCase(
							FormFieldEnumNvp.getName(), FormFieldEnumDescription)) {
						FormFieldEnumNvp.setValue(formSpecificFieldValues.get(FormFieldEnum));
						filledFields++;
						break;
					}

				}
		}
		
		Logger.getLogger("floep.util.FormUtils").info("Form filled with " + filledFields + " values.");
	}
	
	public static Enums.FormTypeEnum determineFormType(Form form, RegexPack regexPack)
	{
		
		int fieldMatches = 0;
		int mostFieldMatches = fieldMatches;
		FormTypeEnum bestMatch = Enums.FormTypeEnum.UNDETERMINED;
		
		for(NameValuePair nvp : form.getInputFields())
		{
			for(Enums.FormTypeEnum formTypeEnum : Enums.FormTypeEnum.values())
			{
				fieldMatches = 0;
				
				HashMap<Enums.FormFieldEnum, String[]> formFields = regexPack.getFormFieldDescriptions().get(formTypeEnum);
				
				for(Enums.FormFieldEnum formFieldKey : formFields.keySet())
				{	
					for(String formFieldNameRegex :  formFields.get(formFieldKey))
					{
						if(FloepUtils.matchesIgnoreCase(nvp.getValue(), formFieldNameRegex))
						{
							fieldMatches++;
						}
							
					}
					
				}
				
				if(fieldMatches > mostFieldMatches)
				{
					mostFieldMatches = fieldMatches;
					bestMatch = formTypeEnum;
				}
				
				
			}
		}
		
		return bestMatch;
	}
}

