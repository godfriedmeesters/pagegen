package pagegen.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Proxy {
	private Integer id;
	private String ip;
	private int port;
	private Date lastTimeChecked;
	private ProxySource proxySource;

	public Proxy() {
	}

	public Proxy(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Date getLastTimeChecked() {
		return lastTimeChecked;
	}

	public void setLastTimeChecked(Date lastTimeChecked) {
		this.lastTimeChecked = lastTimeChecked;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	public ProxySource getProxySource() {
		return proxySource;
	}

	public void setProxySource(ProxySource proxySource) {
		this.proxySource = proxySource;
	}

}
