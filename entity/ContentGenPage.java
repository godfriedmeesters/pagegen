package pagegen.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ContentGenPage extends Page {
	private Keyword seedKeyword;
	private List<Sentence> sentences;
	private Integer level;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContentGenPage(PageUrl pageUrl, Keyword seedKeyword) {
		super(pageUrl);
		this.seedKeyword = seedKeyword;
		this.level = 1;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public ContentGenPage(PageUrl pageUrl, ContentGenPage parent) {
		super(pageUrl);
		this.level = parent.getLevel() + 1;
		this.seedKeyword = parent.getSeedKeyword();
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public Keyword getSeedKeyword() {
		return seedKeyword;
	}

	public void setSeedKeyword(Keyword keyword) {
		this.seedKeyword = keyword;
	}

	@OneToMany(cascade = CascadeType.ALL)
	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}
}
