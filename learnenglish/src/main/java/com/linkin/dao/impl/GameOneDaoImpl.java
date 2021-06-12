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

import com.linkin.dao.GameOneDao;
import com.linkin.entity.GameOne;
import com.linkin.entity.Music;
import com.linkin.entity.Unit;
import com.linkin.model.SearchGameOneDTO;

@Transactional
@Repository
public class GameOneDaoImpl implements GameOneDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void add(GameOne gameOne ) {
		entityManager.persist(gameOne);
	}

	@Override
	public void update(GameOne gameOne ) {
		entityManager.merge(gameOne);
	}

	@Override
	public void delete(GameOne gameOne ) {
		entityManager.remove(gameOne);
	}

	@Override
	public GameOne getById(Long id) {
		return entityManager.find(GameOne.class, id);
	}

	@Override
	public List<GameOne> find(SearchGameOneDTO searchGameOneDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<GameOne> criteriaQuery = criteriaBuilder.createQuery(GameOne.class);
		Root<GameOne> root = criteriaQuery.from(GameOne.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchGameOneDTO.getKeyword())) {
			Predicate predicate1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchGameOneDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate1));
		}

		if (searchGameOneDTO.getUnitId() != null) {
			Join<Music, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchGameOneDTO.getUnitId()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchGameOneDTO.getSortBy().getData(), "id")) {
			if (searchGameOneDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			}
		}

		TypedQuery<GameOne> typedQuery = entityManager.createQuery(criteriaQuery.select(root));

		if (searchGameOneDTO.getStart() != null) {
			typedQuery.setFirstResult((searchGameOneDTO.getStart()));
			typedQuery.setMaxResults(searchGameOneDTO.getLength());
		}
		return typedQuery.getResultList();

	}

	@Override
	public Long count(SearchGameOneDTO searchGameOneDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<GameOne> root = criteriaQuery.from(GameOne.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchGameOneDTO.getKeyword())) {
			Predicate predicate1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchGameOneDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate1));
		}

		if (searchGameOneDTO.getUnitId() != null) {
			Join<Music, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchGameOneDTO.getUnitId()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();

	}

	@Override
	public Long countTotal(SearchGameOneDTO searchGameOneDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<GameOne> root = criteriaQuery.from(GameOne.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}
}
