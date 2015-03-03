package com.nvisia.sample.dao.jpa;

import com.nvisia.sample.dao.PersonDAO;
import com.nvisia.sample.dao.entity.PersonEntity;
import com.nvisia.sample.dto.Person;

public class PersonJpaDAO extends BaseJpaDAO<PersonEntity, Long, Person> implements PersonDAO {

	@Override
	protected Long extractPrimaryKey(PersonEntity entity) {
		return entity.getIdentifier();
	}

	@Override
	protected PersonEntity disassemble(Person transferObject) {
		PersonEntity toReturn = null;
		if (transferObject != null) {
			toReturn = new PersonEntity();
			toReturn.setFirstName(transferObject.getFirstName());
			toReturn.setLastName(transferObject.getLastName());
			toReturn.setMiddleInitial(transferObject.getMiddleInitial());
			toReturn.setIdentifier(transferObject.getIdentifier());
			toReturn.setDateOfBirth(transferObject.getDateOfBirth());
			
		}
		return toReturn;
	}

	@Override
	protected Person assemble(PersonEntity entity) {
		Person toReturn = null;
		if (entity != null) {
			toReturn = new Person();
			toReturn.setFirstName(entity.getFirstName());
			toReturn.setLastName(entity.getLastName());
			toReturn.setMiddleInitial(entity.getMiddleInitial());
			toReturn.setIdentifier(entity.getIdentifier());
			toReturn.setDateOfBirth(entity.getDateOfBirth());
		}
		return toReturn;
	}

}
