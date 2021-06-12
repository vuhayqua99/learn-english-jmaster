package com.linkin.dao;

import java.util.List;

import com.linkin.entity.Role;
import com.linkin.model.SearchDTO;

public interface RoleDao {
	void add(Role role);

	void update(Role role);

	void delete(Role role);

	List<Role> find(SearchDTO searchDTO);

	long count(SearchDTO searchDTO);

	long countTotal(SearchDTO searchDTO);

	Role getById(Long id);

}
