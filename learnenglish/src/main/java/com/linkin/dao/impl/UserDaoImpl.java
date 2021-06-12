package com.linkin.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.linkin.dao.UserDao;
import com.linkin.entity.Role;
import com.linkin.entity.User;
import com.linkin.model.SearchUserDTO;

@Repository
@Transactional
public class UserDaoImpl extends JPARepository<User> implements UserDao {

	@Override
	public List<User> find(SearchUserDTO searchUserDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);

		// Constructing list of parameters
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (StringUtils.isNotBlank(searchUserDTO.getKeyword())) {
			Predicate predicate1 = builder.like(builder.lower(root.get("phone")),
					"%" + searchUserDTO.getKeyword().toLowerCase() + "%");
			Predicate predicate2 = builder.like(builder.lower(root.get("name")),
					"%" + searchUserDTO.getKeyword().toLowerCase() + "%");

			predicates.add(builder.or(predicate2, predicate1));
		}

		if (searchUserDTO.getRoleList() != null) {
			Join<User, Role> role = root.join("role");
			predicates.add(role.get("id").in(searchUserDTO.getRoleList()));
		}

		if (searchUserDTO.getEnabled() != null) {
			predicates.add(builder.equal(root.get("enabled"), searchUserDTO.getEnabled()));
		}

		if (searchUserDTO.getCreatedById() != null) {
			Join<User, User> createdBy = root.join("createdBy");
			predicates.add(builder.equal(createdBy.get("id"), searchUserDTO.getCreatedById()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		// order
		if (StringUtils.equals(searchUserDTO.getSortBy().getData(), "id")) {
			if (searchUserDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(builder.asc(root.get("id")));
			} else {
				criteriaQuery.orderBy(builder.desc(root.get("id")));
			}
		} else if (StringUtils.equals(searchUserDTO.getSortBy().getData(), "name")) {
			if (searchUserDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(builder.asc(root.get("name")));
			} else {
				criteriaQuery.orderBy(builder.desc(root.get("name")));
			}
		} else if (StringUtils.equals(searchUserDTO.getSortBy().getData(), "createdDate")) {
			if (searchUserDTO.getSortBy().isAsc()) {
				criteriaQuery.orderBy(builder.asc(root.get("createdDate")));
			} else {
				criteriaQuery.orderBy(builder.desc(root.get("createdDate")));
			}
		}

		TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery.select(root));
		if (searchUserDTO.getStart() != null) {
			typedQuery.setFirstResult(searchUserDTO.getStart());
			typedQuery.setMaxResults(searchUserDTO.getLength());
		}
		return typedQuery.getResultList();
	}

	@Override
	public long count(SearchUserDTO searchUserDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<User> root = criteriaQuery.from(User.class);

		// Constructing list of parameters
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (StringUtils.isNotBlank(searchUserDTO.getKeyword())) {
			Predicate predicate1 = builder.like(builder.lower(root.get("phone")),
					"%" + searchUserDTO.getKeyword().toLowerCase() + "%");
			Predicate predicate2 = builder.like(builder.lower(root.get("name")),
					"%" + searchUserDTO.getKeyword().toLowerCase() + "%");

			predicates.add(builder.or(predicate2, predicate1));
		}

		if (searchUserDTO.getRoleList() != null) {
			Join<User, Role> role = root.join("role");
			predicates.add(role.get("id").in(searchUserDTO.getRoleList()));
		}

		if (searchUserDTO.getEnabled() != null) {
			predicates.add(builder.equal(root.get("enabled"), searchUserDTO.getEnabled()));
		}

		if (searchUserDTO.getCreatedById() != null) {
			Join<User, User> createdBy = root.join("createdBy");
			predicates.add(builder.equal(createdBy.get("id"), searchUserDTO.getCreatedById()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(builder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public long countTotal(SearchUserDTO searchUserDTO) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<User> root = criteriaQuery.from(User.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (searchUserDTO.getRoleList() != null) {
			Join<User, Role> role = root.join("role");
			predicates.add(role.get("id").in(searchUserDTO.getRoleList()));
		}

		if (searchUserDTO.getCreatedById() != null) {
			Join<User, User> createdBy = root.join("createdBy");
			predicates.add(builder.equal(createdBy.get("id"), searchUserDTO.getCreatedById()));
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery.select(builder.count(root)));
		return typedQuery.getSingleResult();
	}

	@Override
	public User getById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public User getByPhone(String phone) {
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
			Root<User> root = criteriaQuery.from(User.class);
			criteriaQuery.where(builder.equal(builder.lower(root.get("phone")), phone));
			TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery.select(root).distinct(true));
			return typedQuery.getSingleResult();
		} catch (NoResultException exception) {
			return null;
		}
	}

}
