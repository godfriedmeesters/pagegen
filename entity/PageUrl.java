package pagegen.entity;

import java.net.MalformedURLException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;

/**
 * Represents the url of a  {@link Page}.
 * 
 * @author gmeester
 *
 */
@Entity
public class PageUrl {
	private Integer id;
	private String stringValue;

	public PageUrl() {
	}
	
	public PageUrl(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public PageUrl(String stringValue, PageUrl baseUrl) {
		try {
			java.net.URL url = new java.net.URL(new java.net.URL(baseUrl.getStringValue()), stringValue.replaceFirst("^../", ""));
			this.stringValue = url.toString();
		} catch (MalformedURLException e) {
			Logger logger = Logger.getLogger("floep.entity.PageUrl");
			logger.error(e);
		}
	}
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(columnDefinition="TEXT")
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	@Override
	public String toString() {
		return getStringValue();
	}
	
}
