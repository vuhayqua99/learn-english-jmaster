package com.linkin.model;

import java.io.Serializable;
import java.util.List;

public class ActiveCodeDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String code;
	private Integer numberOfUsers;
	private List<Long> units;
	private List<UnitDTO> unitDTOs;

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Long> getUnits() {
		return units;
	}

	public void setUnits(List<Long> units) {
		this.units = units;
	}

	public List<UnitDTO> getUnitDTOs() {
		return unitDTOs;
	}

	public void setUnitDTOs(List<UnitDTO> unitDTOs) {
		this.unitDTOs = unitDTOs;
	}

}
