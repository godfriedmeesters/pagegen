package pagegen.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Represents a page retrieved from the internet.
 * 
 * @author gmeester
 * 
 */
@Entity
public class Page {
	private Integer id;

	private PageUrl pageUrl;

	private PageText pageText;

	private Date timeCreated;
	
	protected Page()
	{
		this.timeCreated = new Date();
	}

	public Page(PageText pageText)
	{
		this();
		this.pageText = pageText;
	}
	
	public Page(PageUrl pageUrl) {
		this();
		this.pageUrl = pageUrl;
	}
	
	public Page(PageUrl pageUrl, PageText pageText)
	{
		this();
		this.pageUrl = pageUrl;
		this.pageText = pageText;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public PageText getPageText() {
		return pageText;
	}

	public void setPageText(PageText pageText) {
		this.pageText = pageText;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public PageUrl getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(PageUrl pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

}
