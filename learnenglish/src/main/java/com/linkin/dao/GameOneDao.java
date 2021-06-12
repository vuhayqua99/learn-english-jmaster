package com.linkin.dao;

import java.util.List;

import com.linkin.entity.GameOne;
import com.linkin.model.SearchGameOneDTO;

public interface GameOneDao {
	void add(GameOne gameOne);

	void update(GameOne gameOne);

	void delete(GameOne gameOne);

	GameOne getById(Long id);

	List<GameOne> find(SearchGameOneDTO searchGameOneDTO);

	Long count(SearchGameOneDTO searchGameOneDTO);

	Long countTotal(SearchGameOneDTO searchGameOneDTO);

}
