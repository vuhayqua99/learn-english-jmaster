package com.linkin.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UnitDTO {
	private Long id;
	private String name;
	private String image;

	@JsonIgnore
	private MultipartFile imageFile;

	public String getImage() {
		return image;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public UnitDTO() {
		super();
	}

	public UnitDTO(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
