package pagegen.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PageLayout {
	Integer id;
	String stringValue;
	Integer numberOfLinks;
	
	public PageLayout() {
		// TODO Auto-generated constructor stub
	}
	
	public PageLayout(String pageLayout)
	{
		this.stringValue = pageLayout;
	}
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(String pageLayout) {
		this.stringValue = pageLayout;
	}

	public Integer getNumberOfLinks() {
		return numberOfLinks;
	}

	public void setNumberOfLinks(Integer numberOfLinks) {
		this.numberOfLinks = numberOfLinks;
	}
}
