package pagegen.model;

import pagegen.entity.PageUrl;

public class MailInfo {
	private String password;
	private PageUrl activationUrl;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public PageUrl getActivationUrl() {
		return activationUrl;
	}
	public void setActivationUrl(PageUrl activationUrl) {
		this.activationUrl = activationUrl;
	}
	
	
}
