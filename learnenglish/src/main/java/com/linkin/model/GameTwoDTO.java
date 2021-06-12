package com.linkin.model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class GameTwoDTO {

	private Long id;
	private String name;
	private String audio;
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private Integer correct;
	
	private UnitDTO unitDTO;

	@JsonIgnore
	private MultipartFile audioFile;

	@JsonIgnore
	private MultipartFile imageFile1;
	@JsonIgnore
	private MultipartFile imageFile2;
	@JsonIgnore
	private MultipartFile imageFile3;
	@JsonIgnore
	private MultipartFile imageFile4;

}
