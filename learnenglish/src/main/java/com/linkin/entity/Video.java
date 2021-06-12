package com.linkin.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "video")
public class Video extends FileData {

	private static final long serialVersionUID = 1L;

	public Video() {
		super();
	}

	public Video(Long id) {
		super();
		super.setId(id);
	}

}
