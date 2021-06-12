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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.linkin.dao.UnitDao;
import com.linkin.entity.Unit;
import com.linkin.model.SearchUnitDTO;

@Transactional
@Repository
public class UnitDaoImpl implements UnitDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void add(Unit unit) {
		entityManager.persist(unit);
	}

	@Override
	public void update(Unit unit) {
		entityManager.merge(unit);
	}

	@Override
	public void delete(Long id) {
		entityManager.remove(getById(id));

	}

	@Override
	public Unit getById(Long id) {
		return entityManager.find(Unit.class, id);
	}

	@Override
	public List<Unit> find(SearchUnitDTO searchUnitDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Unit> criteriaQuery = criteriaBuilder.createQuery(Unit.class);
		Root<Unit> root = criteriaQuery.from(Unit.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchUnitDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchUnitDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}

		if (CollectionUtils.isNotEmpty(searchUnitDTO.getUnitIds())) {
			predicates.add(root.get("id").in(searchUnitDTO.getUnitIds()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchUnitDTO.getSortBy().getData(), "id")) {
			if (searchUnitDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			}
		} else if (StringUtils.equals(searchUnitDTO.getSortBy().getData(), "name")) {
			if (searchUnitDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("name")));
			}

		}
		TypedQuery<Unit> typedQuery = entityManager.createQuery(criteriaQuery.select(root));

		if (searchUnitDTO.getStart() != null) {
			typedQuery.setFirstResult((searchUnitDTO.getStart()));
			typedQuery.setMaxResults(searchUnitDTO.getLength());
		}
		return typedQuery.getResultList();

	}

	@Override
	public Long count(SearchUnitDTO searchUnitDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Unit> root = criteriaQuery.from(Unit.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchUnitDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchUnitDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}

		if (CollectionUtils.isNotEmpty(searchUnitDTO.getUnitIds())) {
			predicates.add(root.get("id").in(searchUnitDTO.getUnitIds()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();

	}

	@Override
	public Long countTotal(SearchUnitDTO searchUnitDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Unit> root = criteriaQuery.from(Unit.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}
}
