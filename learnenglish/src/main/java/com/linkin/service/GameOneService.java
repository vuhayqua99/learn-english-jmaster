package com.linkin.service;

import java.util.List;

import com.linkin.model.GameOneDTO;
import com.linkin.model.SearchGameOneDTO;

public interface GameOneService {
	void add(GameOneDTO gameOneDTO);

	void delete(Long id);

	void edit(GameOneDTO gameOneDTO);

	GameOneDTO getById(Long id);

	Long count(SearchGameOneDTO searchGameOneDTO);

	Long countTotal(SearchGameOneDTO searchGameOneDTO);

	List<GameOneDTO> find(SearchGameOneDTO searchGameOneDTO);
}
