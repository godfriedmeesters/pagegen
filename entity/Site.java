package pagegen.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Site {
	Integer id;
	GeneratedPage homePage;
	PageLayout defaultPageLayout;

	public Site() {
		// TODO Auto-generated constructor stub
	}
	
	public Site(GeneratedPage homePage, PageLayout pageLayout) {
		this.homePage = homePage;
		this.defaultPageLayout = pageLayout;
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
	public GeneratedPage getHomePage() {
		return homePage;
	}
	
	public void setHomePage(GeneratedPage homePage) {
		this.homePage = homePage;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public PageLayout getDefaultPageLayout() {
		return defaultPageLayout;
	}

	public void setDefaultPageLayout(PageLayout defaultPageLayout) {
		this.defaultPageLayout = defaultPageLayout;
	}
}

