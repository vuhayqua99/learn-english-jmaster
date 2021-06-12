package com.linkin.service;

import java.util.List;

import com.linkin.model.SearchGameTwoDTO;
import com.linkin.model.GameTwoDTO;

public interface GameTwoService {
	void add(GameTwoDTO gameTwoDTO);

	void delete(Long id);

	void edit(GameTwoDTO gameTwoDTO);

	GameTwoDTO getById(Long id);

	List<GameTwoDTO> find(SearchGameTwoDTO searchGameTwoDTO);

	Long count(SearchGameTwoDTO searchGameTwoDTO);

	Long countTotal(SearchGameTwoDTO searchGameTwoDTO);
}
