package pagegen.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Target {
	private Integer id;
	private PageUrl pageUrl;
	private Keyword relatedKeyword;
	private TargetType targetType;

	@ManyToOne(cascade = CascadeType.ALL)
	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public Keyword getRelatedKeyword() {
		return relatedKeyword;
	}

	public void setRelatedKeyword(Keyword relatedKeyword) {
		this.relatedKeyword = relatedKeyword;
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
}
