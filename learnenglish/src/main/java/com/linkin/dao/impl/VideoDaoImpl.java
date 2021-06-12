package com.linkin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linkin.dao.VideoDao;
import com.linkin.entity.Unit;
import com.linkin.entity.Video;
import com.linkin.model.SearchVideoDTO;

@Repository
@Transactional
public class VideoDaoImpl implements VideoDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void add(Video video) {
		entityManager.persist(video);
	}

	@Override
	public void delete(Video video) {
		entityManager.remove(video);
	}

	@Override
	public void update(Video video) {
		entityManager.merge(video);
	}

	@Override
	public Video getById(Long id) {
		return entityManager.find(Video.class, id);
	}

	@Override
	public List<Video> find(SearchVideoDTO searchVideoDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Video> criteriaQuery = criteriaBuilder.createQuery(Video.class);
		Root<Video> root = criteriaQuery.from(Video.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchVideoDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchVideoDTO.getKeyword().toLowerCase() + "%");

			predicates.add(predicate);
		}
		
		if (searchVideoDTO.getUnitId() != null) {
			Join<Video, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchVideoDTO.getUnitId()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchVideoDTO.getSortBy().getData(), "id")) {
			if (searchVideoDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			}
		}
		
		TypedQuery<Video> typedQuery = entityManager.createQuery(criteriaQuery.select(root));

		if (searchVideoDTO.getStart() != null) {
			typedQuery.setFirstResult((searchVideoDTO.getStart()));
			typedQuery.setMaxResults(searchVideoDTO.getLength());
		}

		return typedQuery.getResultList();
	}

	@Override
	public Long count(SearchVideoDTO searchVideoDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Video> root = criteriaQuery.from(Video.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchVideoDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchVideoDTO.getKeyword().toLowerCase() + "%");

			predicates.add(predicate);
		}
		
		if (searchVideoDTO.getUnitId() != null) {
			Join<Video, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchVideoDTO.getUnitId()));
		}


		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public Long coutTotal(SearchVideoDTO searchVideoDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Video> root = criteriaQuery.from(Video.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

}
