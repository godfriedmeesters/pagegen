package pagegen.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Keyword {
	private Integer id;
	private String stringValue;
	private List<ContentGenPage> contentGenPages;
	private List<GeneratedPage> generatedPages;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<ContentGenPage> getContentGenPages() {
		return contentGenPages;
	}

	public void setContentGenPages(List<ContentGenPage> contentGenPages) {
		this.contentGenPages = contentGenPages;
	}

	@OneToMany(cascade = CascadeType.ALL)
	public List<GeneratedPage> getGeneratedPages() {
		return generatedPages;
	}

	public void setGeneratedPages(List<GeneratedPage> generatedPages) {
		this.generatedPages = generatedPages;
	}

	public Keyword() {
	}
	
	public Keyword(String stringValue) {
		this.stringValue = stringValue;
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

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	@Override
	public String toString() {
		return stringValue;
	}
	
}
