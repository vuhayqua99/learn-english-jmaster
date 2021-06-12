package com.linkin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.linkin.dao.GameTwoDao;
import com.linkin.entity.GameOne;
import com.linkin.entity.GameTwo;
import com.linkin.entity.Unit;
import com.linkin.entity.Video;
import com.linkin.model.SearchGameTwoDTO;

@Transactional
@Repository
public class GameTwoDaoimpl implements GameTwoDao {

	@PersistenceContext
	private EntityManager entitymanager;

	@Override
	public void add(GameTwo CategoryUnitGame) {
		entitymanager.persist(CategoryUnitGame);
	}

	@Override
	public void update(GameTwo CategoryUnitGame) {
		entitymanager.merge(CategoryUnitGame);
	}

	@Override
	public void delete(Long id) {
		entitymanager.remove(getById(id));
	}

	@Override
	public GameTwo getById(Long id) {
		return entitymanager.find(GameTwo.class, id);
	}

	@Override
	public List<GameTwo> find(SearchGameTwoDTO searchCategoryUnitGameDTO) {
		CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
		CriteriaQuery<GameTwo> criteriaQuery = criteriaBuilder.createQuery(GameTwo.class);
		Root<GameTwo> root = criteriaQuery.from(GameTwo.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchCategoryUnitGameDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchCategoryUnitGameDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}

		if (searchCategoryUnitGameDTO.getUnitId() != null) {
			Join<Video, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchCategoryUnitGameDTO.getUnitId()));
		}


		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchCategoryUnitGameDTO.getSortBy().getData(), "id")) {
			if (searchCategoryUnitGameDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			}
		} else if (StringUtils.equals(searchCategoryUnitGameDTO.getSortBy().getData(), "name")) {
			if (searchCategoryUnitGameDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("name")));
			}

		}
		TypedQuery<GameTwo> typedQuery = entitymanager.createQuery(criteriaQuery.select(root));

		if (searchCategoryUnitGameDTO.getStart() != null) {
			typedQuery.setFirstResult((searchCategoryUnitGameDTO.getStart()));
			typedQuery.setMaxResults(searchCategoryUnitGameDTO.getLength());
		}
		return typedQuery.getResultList();
	}

	@Override
	public Long count(SearchGameTwoDTO searchCategoryUnitGameDTO) {
		CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<GameTwo> root = criteriaQuery.from(GameTwo.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchCategoryUnitGameDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchCategoryUnitGameDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}
		
		if (searchCategoryUnitGameDTO.getUnitId() != null) {
			Join<Video, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchCategoryUnitGameDTO.getUnitId()));
		}


		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entitymanager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public Long countTotal(SearchGameTwoDTO searchCategoryUnitGameDTO) {
		CriteriaBuilder criteriaBuilder = entitymanager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<GameTwo> root = criteriaQuery.from(GameTwo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		if (searchCategoryUnitGameDTO.getUnitId() != null) {
			Join<Video, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchCategoryUnitGameDTO.getUnitId()));
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entitymanager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

}
