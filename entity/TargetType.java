package pagegen.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import pagegen.util.Enums.SearchEngineEnum;

@Entity
public class TargetType {
	private Integer id;
	private String description;
	private String googleSearchString;
	private String msnSearchString;
	private String yahooSearchString;

	public TargetType() {}
	
	public TargetType(String description, String googleSearchString, String msnSearchString, String yahooSearchString) {
		this.description = description;
		this.googleSearchString = googleSearchString;
		this.yahooSearchString = yahooSearchString;
		this.msnSearchString = msnSearchString;
	}
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGoogleSearchString() {
		return googleSearchString;
	}

	public void setGoogleSearchString(String googleSearchString) {
		this.googleSearchString = googleSearchString;
	}

	public String getMsnSearchString() {
		return msnSearchString;
	}

	public void setMsnSearchString(String msnSearchString) {
		this.msnSearchString = msnSearchString;
	}

	public String getYahooSearchString() {
		return yahooSearchString;
	}

	public void setYahooSearchString(String yahooSearchString) {
		this.yahooSearchString = yahooSearchString;
	}

	public String getSearchString(SearchEngineEnum see) {
		switch (see) {
		case GOOGLE:
			return getGoogleSearchString();
		case YAHOO:
			return getYahooSearchString();
		case MSN:
			return getMsnSearchString();
		default:
			return null;
		}
	}
}
