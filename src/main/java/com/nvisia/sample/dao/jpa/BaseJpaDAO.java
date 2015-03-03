package com.nvisia.sample.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class used to provide common JPA-based functionality for DAO
 * implementations. DAO interfaces will be able to define methods (save(TO),
 * update(TO), delete(PK), etc) that adhere to the public methods exposed on
 * this class. This allows sub-classes to have to implement the similar CRUD
 * methods, as long as the interface was properly written.
 *
 * @param <E>
 *            The JPA-annotated {@link Entity} object.
 * @param <PK>
 *            The serializable primiary key object used to lookup the E (entity)
 *            object.
 * @param <TO>
 *            The data-transfer object which is exposed via the DAO interface.
 *            This DTO should mirror the Entity object, only exposing the data
 *            needed by consumers
 */
public abstract class BaseJpaDAO <E, PK extends Serializable, TO> {

	private Class<E> classToBePersisted;
	private EntityManager entityManager;

	protected BaseJpaDAO() {
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
	public TO findById(PK id) {
		return assemble(getEntityManager().find(classToBePersisted, id));
	}
	
	@Transactional
	public final PK save(TO transferObject) {
		final E toPersist = disassemble(transferObject);
		getEntityManager().persist(toPersist);
		return extractPrimaryKey(toPersist);
	}
	
	@Transactional
	public void update(TO transferObject) {
		final E toPersist = disassemble(transferObject);
		getEntityManager().merge(toPersist);
	}
	
	@Transactional
	public void delete(PK id) {
		final E toDelete = getEntityManager().find(classToBePersisted, id);
		if (toDelete != null) {
			getEntityManager().remove(toDelete);
		}
	}
	
	@Transactional(readOnly = true)
	public List<TO> findAll() {
		return findAll(-1);
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<TO> findAll(int number) {
		final List<TO> toReturn = new ArrayList<TO>();
		final Query query = getEntityManager().createQuery(
				"select E from " + classToBePersisted.getSimpleName() + " E");
		if (number > 0) {
			query.setMaxResults(number);
		}
		
		final List<E> entities = query.getResultList();
		if (CollectionUtils.isNotEmpty(entities)) {
			for (E entity : entities) {
				toReturn.add(assemble(entity));
			}
		}
		return toReturn;
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
	
	protected abstract E disassemble(TO transferObject);
	
	protected abstract TO assemble(E entity);
}