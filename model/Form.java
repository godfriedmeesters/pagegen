package pagegen.model;

import java.util.List;

import org.apache.commons.httpclient.NameValuePair;

import pagegen.entity.PageUrl;
import pagegen.util.Enums;
import pagegen.util.Enums.FormTypeEnum;

public class Form {
	private String formName;
	private PageUrl actionUrl;
	private List<NameValuePair> inputFields;
	private List<NameValuePair> hiddenFields;
	private List<NameValuePair> submitButtons;
	private Enums.FormTypeEnum formTypeEnum = FormTypeEnum.UNDETERMINED;
	
	Enums.FormTypeEnum formType;
	
	public Enums.FormTypeEnum getFormType() {
		return formType;
	}
	public void setFormType(Enums.FormTypeEnum formType) {
		this.formType = formType;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public PageUrl getActionUrl() {
		return actionUrl;
	}
	public void setActionUrl(PageUrl actionUrl) {
		this.actionUrl = actionUrl;
	}
	public List<NameValuePair> getInputFields() {
		return inputFields;
	}
	public void setInputFields(List<NameValuePair> inputFields) {
		this.inputFields = inputFields;
	}
	public List<NameValuePair> getHiddenFields() {
		return hiddenFields;
	}
	public void setHiddenFields(List<NameValuePair> hiddenFields) {
		this.hiddenFields = hiddenFields;
	}
	public List<NameValuePair> getSubmitButtons() {
		return submitButtons;
	}
	public void setSubmitButtons(List<NameValuePair> submitButtons) {
		this.submitButtons = submitButtons;
	}
	public Enums.FormTypeEnum getFormTypeEnum() {
		return formTypeEnum;
	}
	public void setFormTypeEnum(Enums.FormTypeEnum formTypeEnum) {
		this.formTypeEnum = formTypeEnum;
	}
}
