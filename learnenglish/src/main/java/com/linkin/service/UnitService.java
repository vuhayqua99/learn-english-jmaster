package com.linkin.service;

import java.util.List;

import com.linkin.model.UnitDTO;
import com.linkin.model.SearchUnitDTO;

public interface UnitService {

	void add(UnitDTO unitDTO);

	void delete(Long id);

	void edit(UnitDTO unitDTO);

	UnitDTO getById(Long id);

	List<UnitDTO> find(SearchUnitDTO searchUnitDTO);

	Long count(SearchUnitDTO searchUnitDTO);

	Long countTotal(SearchUnitDTO searchUnitDTO);

}
