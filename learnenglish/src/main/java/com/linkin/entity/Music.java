package com.linkin.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "music")
public class Music extends FileData {

	private static final long serialVersionUID = 1L;

	public Music() {
	}

	public Music(Long id) {
		super();
		super.setId(id);
	}

}
