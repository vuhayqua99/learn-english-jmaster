package com.linkin.dao;

import java.util.List;

import com.linkin.entity.Unit;
import com.linkin.model.SearchUnitDTO;

public interface UnitDao {

	void add(Unit unit);

	void update(Unit unit);

	void delete(Long id);

	Unit getById(Long id);

	List<Unit> find(SearchUnitDTO searchUnitDTO);

	Long count(SearchUnitDTO searchUnitDTO);

	Long countTotal(SearchUnitDTO searchUnitDTO);

	

}
