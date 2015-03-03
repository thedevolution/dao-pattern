package com.nvisia.sample.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

/**
 * Base-JPA that does NOT transform between JPA-specific entity and DTO objects.
 * This class is specifically created to demonstrate traditional DAO base-class
 * which streamline development, but do not adhere to the core DAO pattern.
 *
 * @param <E>
 *            The JPA-annotated {@link Entity} object.
 * @param <PK>
 *            The serializable primiary key object used to lookup the E (entity)
 *            object.
 */
public abstract class BaseJpaDAOWithoutDTO<E, PK extends Serializable> {

	private Class<E> classToBePersisted;
	private EntityManager entityManager;

	protected BaseJpaDAOWithoutDTO() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			setParameterizedType((ParameterizedType) type);
		} else {
			type = getClass().getSuperclass().getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				setParameterizedType((ParameterizedType) type);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setParameterizedType(ParameterizedType parameterizedType) {
		this.classToBePersisted = (Class<E>) parameterizedType
				.getActualTypeArguments()[0];
	}

	protected Class<E> getClassToBePersisted() {
		return this.classToBePersisted;
	}

	@Transactional(readOnly = true)
	public E findById(PK id) {
		return getEntityManager().find(classToBePersisted, id);
	}

	@Transactional
	public final PK save(E entity) {
		getEntityManager().persist(entity);
		return extractPrimaryKey(entity);
	}

	@Transactional
	public boolean update(E entity) {
		// If the update succeeds, then our merge return instance will not
		// be null.
		return getEntityManager().merge(entity) != null;
	}

	@Transactional
	public void delete(PK id) {
		E toDelete = findById(id);
		if (toDelete != null) {
			getEntityManager().remove(toDelete);
		}
	}

	@Transactional(readOnly = true)
	public List<E> findAll() {
		return findAll(-1);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<E> findAll(int number) {
		final Query query = getEntityManager().createQuery(
				"select E from " + classToBePersisted.getSimpleName() + " E");
		if (number > 0) {
			query.setMaxResults(number);
		}

		return query.getResultList();
	}

	@Transactional(readOnly = true)
	public long count() {
		return (Long) getEntityManager().createQuery(
				"select count(E) from " + classToBePersisted.getSimpleName()
						+ " E").getSingleResult();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Method defining the setting of the JPA EntityManager. The persistence.xml
	 * in the META-INF folder specifies the "DefaultDBUnit" definition.
	 * 
	 * @param entityManager
	 *            The JPA entity manager.
	 */
	@PersistenceContext(unitName = "DefaultDBUnit")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Method used to get the PK attribute from the given entity.
	 * 
	 * @param entity
	 *            The entity persistent class.
	 * @return Returns the id instance. Typically returns an integer or long.
	 */
	protected abstract PK extractPrimaryKey(E entity);

}
