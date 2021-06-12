package com.linkin.dao;

import java.util.List;

import com.linkin.entity.ActiveCode;
import com.linkin.model.SearchActiveCodeDTO;

public interface ActiveCodeDao {
	
	void add(ActiveCode activeCode);

	List<ActiveCode> find(SearchActiveCodeDTO searchAffiliateDTO);

	Long count(SearchActiveCodeDTO searchAffiliateDTO);

	Long countTotal(SearchActiveCodeDTO searchAffiliateDTO);

}
