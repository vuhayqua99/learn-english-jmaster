package com.linkin.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameOneDTO {
	private Long id;
	private String image;
	private String audio;
	private String name;
	private Long unitId;
	private String unitName;
	
	@JsonIgnore
	private MultipartFile imageFile;
	
	@JsonIgnore
	private MultipartFile audioFile;
	
	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public MultipartFile getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(MultipartFile audioFile) {
		this.audioFile = audioFile;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public GameOneDTO(Long id) {
		super();
		this.id = id;
	}

	public GameOneDTO() {
		super();
	}
	
	
}
