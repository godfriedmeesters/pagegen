package pagegen.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents the contents of a  {@link Page}.
 * 
 * @author gmeester
 *
 */
@Entity
public class PageText {
	private Integer id;
	private String stringValue;
	
	public PageText(String stringValue) {
		this.stringValue = stringValue;
	}
	
    public PageText() {
	}
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(columnDefinition="LONGTEXT")
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
