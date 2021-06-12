package com.linkin.dao;

import java.util.List;

import com.linkin.entity.GameTwo;
import com.linkin.model.SearchGameTwoDTO;

public interface GameTwoDao {
	void add(GameTwo CategoryUnitGame);

	void update(GameTwo CategoryUnitGame);

	void delete(Long id);

	GameTwo getById(Long id);

	List<GameTwo> find(SearchGameTwoDTO searchCategoryUnitGameDTO);

	Long count(SearchGameTwoDTO searchCategoryUnitGameDTO);

	Long countTotal(SearchGameTwoDTO searchCategoryUnitGameDTO);
}
