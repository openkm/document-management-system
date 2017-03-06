package com.openkm.dao.bean;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OKM_LOCK_TOKEN", uniqueConstraints =
@UniqueConstraint(name = "IDX_LOCK_TOK_TOK", columnNames = "LT_TOKEN"))
public class LockToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LT_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "LT_USER", length = 64)
	private String user;

	@Column(name = "LT_TOKEN", length = 64)
	private String token;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", user=");
		sb.append(user);
		sb.append(", token=");
		sb.append(token);
		sb.append("}");
		return sb.toString();
	}
}
