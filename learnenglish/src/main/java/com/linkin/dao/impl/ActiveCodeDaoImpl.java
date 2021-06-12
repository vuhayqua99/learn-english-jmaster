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

import com.linkin.dao.ActiveCodeDao;
import com.linkin.entity.ActiveCode;
import com.linkin.entity.User;
import com.linkin.model.SearchActiveCodeDTO;

@Transactional
@Repository
public class ActiveCodeDaoImpl implements ActiveCodeDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void add(ActiveCode activeCode) {
		entityManager.persist(activeCode);
	}

	@Override
	public List<ActiveCode> find(SearchActiveCodeDTO searchAffiliateDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ActiveCode> criteriaQuery = criteriaBuilder.createQuery(ActiveCode.class);
		Root<ActiveCode> root = criteriaQuery.from(ActiveCode.class);
		Join<ActiveCode, User> user = root.join("user");

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchAffiliateDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(user.get("phone")),
					"%" + searchAffiliateDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		// order
		if (StringUtils.equals(searchAffiliateDTO.getSortBy().getData(), "createdDate")) {
			if (searchAffiliateDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("createdDate")));
			} else {
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
			}
		}

		TypedQuery<ActiveCode> typedQuery = entityManager.createQuery(criteriaQuery.select(root));

		if (searchAffiliateDTO.getStart() != null) {
			typedQuery.setFirstResult((searchAffiliateDTO.getStart()));
			typedQuery.setMaxResults(searchAffiliateDTO.getLength());
		}
		return typedQuery.getResultList();
	}

	@Override
	public Long count(SearchActiveCodeDTO searchAffiliateDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		Root<ActiveCode> root = criteriaQuery.from(ActiveCode.class);
		Join<ActiveCode, User> user = root.join("user");

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (StringUtils.isNotBlank(searchAffiliateDTO.getKeyword())) {
			Predicate predicate = criteriaBuilder.like(criteriaBuilder.lower(user.get("phone")),
					"%" + searchAffiliateDTO.getKeyword().toLowerCase() + "%");

			predicates.add((predicate));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public Long countTotal(SearchActiveCodeDTO searchAffiliateDTO) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<ActiveCode> root = criteriaQuery.from(ActiveCode.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(criteriaBuilder.count(root)));
		return typedQuery.getSingleResult();
	}

}
