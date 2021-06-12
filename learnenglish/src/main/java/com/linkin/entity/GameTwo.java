package com.linkin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "game_two")
public class GameTwo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "audio")
	private String audio;

	@Column(name = "image_1")
	private String image1;

	@Column(name = "image_2")
	private String image2;

	@Column(name = "image_3")
	private String image3;

	@Column(name = "image_4")
	private String image4;

	@Column(name = "correct")
	private Integer correct;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private Unit unit;
	
	
}
