package com.linkin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.linkin.dao.RoleDao;
import com.linkin.entity.Role;
import com.linkin.model.SearchDTO;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao {

	@PersistenceContext
	protected EntityManager entityManager;

	public void add(Role role) {
		entityManager.persist(role);
	}

	public void update(Role role) {
		entityManager.merge(role);
	}

	public void delete(Role role) {
		entityManager.remove(role);
	}

	@Override
	public List<Role> find(SearchDTO searchDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);
		Root<Role> root = criteriaQuery.from(Role.class);
		// Constructing list of parameters
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchDTO.getKeyword())) {
			Predicate predicate = builder.like(builder.lower(root.get("name")),
					"%" + searchDTO.getKeyword().toLowerCase() + "%");
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchDTO.getSortBy().getData(), "id")) {
			if (searchDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(builder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(builder.desc(root.get("id")));
			}
		} else if (StringUtils.equals(searchDTO.getSortBy().getData(), "name")) {
			if (searchDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(builder.asc(root.get("name")));
			} else {
				criteriaQuery.orderBy(builder.desc(root.get("name")));
			}
		}

		TypedQuery<Role> typedQuery = entityManager.createQuery(criteriaQuery.select(root));
		if (searchDTO.getStart() != null) {
			typedQuery.setFirstResult(searchDTO.getStart());
			typedQuery.setMaxResults(searchDTO.getLength());
		}
		return typedQuery.getResultList();
	}

	@Override
	public long count(SearchDTO searchDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<Role> root = criteriaQuery.from(Role.class);
		// Constructing list of parameters
		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchDTO.getKeyword())) {
			Predicate predicate = builder.like(builder.lower(root.get("name")),
					"%" + searchDTO.getKeyword().toLowerCase() + "%");
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(builder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public long countTotal(SearchDTO searchDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<Role> root = criteriaQuery.from(Role.class);

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(builder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public Role getById(Long id) {
		return entityManager.find(Role.class, id);
	}

}
