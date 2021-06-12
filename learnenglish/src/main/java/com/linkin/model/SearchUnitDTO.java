package com.linkin.model;

import java.util.Set;

public class SearchUnitDTO extends SearchDTO {
	private static final long serialVersionUID = 1L;

	private Set<Long> unitIds;

	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public Set<Long> getUnitIds() {
		return unitIds;
	}

	public void setUnitIds(Set<Long> unitIds) {
		this.unitIds = unitIds;
	}

}
