package pagegen.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class GeneratedPage extends Page {
	private GeneratedPage parent;
	private Keyword seedKeyword;
	private List<GeneratedPage> children;
	private int generatedFromlevel;
	private PageLayout pageLayout;
	private Site site;
	
	public GeneratedPage() {
		this.children = new ArrayList<GeneratedPage>();
	}
	
	public GeneratedPage(Keyword seedKeyword, PageText pageText, int level) {
		this();
		this.seedKeyword = seedKeyword;
		setPageText(pageText);
		setGeneratedFromLevel(level);
	}
	@ManyToOne(cascade = CascadeType.ALL)
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public PageLayout getPageLayout() {
		return pageLayout;
	}

	public void setPageLayout(PageLayout pageLayout) {
		this.pageLayout = pageLayout;
	}



	@ManyToOne(cascade = CascadeType.ALL)
	public Keyword getSeedKeyword() {
		return seedKeyword;
	}
	
	public void setSeedKeyword(Keyword seedKeyword) {
		this.seedKeyword = seedKeyword;
	}
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<GeneratedPage> getChildren() {
		return children;
	}

	public void setChildren(List<GeneratedPage> links) {
		this.children = links;
	}	
	
	public int getGeneratedFromLevel() {
		return generatedFromlevel;
	}

	public void setGeneratedFromLevel(int generatedFromLevel) {
		this.generatedFromlevel = generatedFromLevel;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public GeneratedPage getParent() {
		return parent;
	}

	public void setParent(GeneratedPage parent) {
		this.parent = parent;
	}
}
