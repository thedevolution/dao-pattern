package com.nvisia.sample.dao;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nvisia.sample.dto.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/nvisia/sample/dao/applicationContext-PersonDAO.xml" })
public class PersonDAOTest {

	@Autowired
	private PersonDAO personDAO;
	
	@Test
	public void testCRUD() throws Exception {
		// Instantiate the Person DTO and populate it with mock data
		final Person toAdd = new Person();
		Calendar birthDate = Calendar.getInstance();
		birthDate.add(Calendar.YEAR, -40);
		toAdd.setDateOfBirth(birthDate.getTime());
		toAdd.setFirstName("Test");
		toAdd.setLastName("TheTester");
		toAdd.setMiddleInitial("T");

		// Persist the Person object and assign the PK to a variable
		final Long generatedID = personDAO.save(toAdd);
		Assert.assertNotNull(generatedID);
		
		// Now look up the persisted Person by the generated ID
		final Person foundByID = personDAO.findById(generatedID);
		Assert.assertNotNull(foundByID);
		
		// Verify this is the same person
		Assert.assertEquals(toAdd.getFirstName(), foundByID.getFirstName());
		Assert.assertEquals(toAdd.getLastName(), foundByID.getLastName());
		
		// Create an incorrect ID so we can try finding a person that does not exist
		final Long incorrectID = new Long(-1);
		Assert.assertNotSame(incorrectID, generatedID);
		
		// Lookup the 
		final Person shouldNotBeFound = personDAO.findById(incorrectID);
		Assert.assertNull(shouldNotBeFound);
		
		foundByID.setFirstName("Tester");
		personDAO.update(foundByID);
		
		final Person wasPersonUpdated = personDAO.findById(generatedID);
		Assert.assertNotNull(wasPersonUpdated);
		Assert.assertEquals(foundByID.getFirstName(), wasPersonUpdated.getFirstName());
		
		personDAO.delete(generatedID);
		
		final Person deletedAndShouldNotBeFound = personDAO.findById(generatedID);
		Assert.assertNull(deletedAndShouldNotBeFound);
	}
}
