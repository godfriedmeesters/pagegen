package pagegen.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ProxySource {
	Integer id;
	PageUrl pageUrl;
	Date lastTimeScraped;
		
	public ProxySource(PageUrl pageUrl) {
		this.pageUrl = pageUrl;
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
	public PageUrl getPageUrl() {
		return pageUrl;
	}
	
	public void setPageUrl(PageUrl pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Date getLastTimeScraped() {
		return lastTimeScraped;
	}

	public void setLastTimeScraped(Date lastTimeScraped) {
		this.lastTimeScraped = lastTimeScraped;
	}
}
