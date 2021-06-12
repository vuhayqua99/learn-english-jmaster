package com.linkin.service;

import java.util.List;

import com.linkin.model.ActiveCodeDTO;
import com.linkin.model.SearchActiveCodeDTO;

public interface ActiveCodeService {

	void add(ActiveCodeDTO activeCodeDTO);

	void update(ActiveCodeDTO activeCodeDTO);

	void delete(Long id);
	
	ActiveCodeDTO get(String phone);

	List<ActiveCodeDTO> find(SearchActiveCodeDTO searchActiveCodeDTO);

	Long count(SearchActiveCodeDTO searchActiveCodeDTO);

	Long countToTal(SearchActiveCodeDTO searchActiveCodeDTO);

	void downCounter(long userId);

	void upCounter(long userId);
}
