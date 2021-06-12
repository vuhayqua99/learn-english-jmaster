package com.linkin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.linkin.dao.MusicDao;
import com.linkin.entity.Music;
import com.linkin.entity.Unit;
import com.linkin.model.SearchMusicDTO;

@Transactional
@Repository
public class MusicDaoImpl implements MusicDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void add(Music music) {
		entityManager.persist(music);
	}

	@Override
	public void update(Music music) {
		entityManager.merge(music);
	}

	@Override
	public void delete(Music music) {
		entityManager.remove(music);
	}

	@Override
	public Music getById(Long id) {
		return entityManager.find(Music.class, id);
	}

	@Override
	public List<Music> find(SearchMusicDTO searchMusicDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Music> criteriaQuery = criteriaBuilder.createQuery(Music.class);
		Root<Music> root = criteriaQuery.from(Music.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchMusicDTO.getKeyword())) {
			Predicate predicate1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchMusicDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate1));
		}

		if (searchMusicDTO.getUnitId() != null) {
			Join<Music, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchMusicDTO.getUnitId()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchMusicDTO.getSortBy().getData(), "id")) {
			if (searchMusicDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			}
		}

		TypedQuery<Music> typedQuery = entityManager.createQuery(criteriaQuery.select(root));

		if (searchMusicDTO.getStart() != null) {
			typedQuery.setFirstResult((searchMusicDTO.getStart()));
			typedQuery.setMaxResults(searchMusicDTO.getLength());
		}
		return typedQuery.getResultList();

	}

	@Override
	public Long count(SearchMusicDTO searchMusicDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Music> root = criteriaQuery.from(Music.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchMusicDTO.getKeyword())) {
			Predicate predicate1 = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
					"%" + searchMusicDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate1));
		}

		if (searchMusicDTO.getUnitId() != null) {
			Join<Music, Unit> unit = root.join("unit");
			predicates.add(criteriaBuilder.equal(unit.get("id"), searchMusicDTO.getUnitId()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();

	}

	@Override
	public Long countTotal(SearchMusicDTO searchMusicDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Music> root = criteriaQuery.from(Music.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}


}
