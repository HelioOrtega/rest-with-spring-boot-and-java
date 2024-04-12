package br.com.helio.integrationtests.repositories;

import br.com.helio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.helio.model.Person;
import br.com.helio.repositories.PersonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setUp(){
        person = new Person();
    }

    @Test
    @Order(0)
    public void testFindPersonByName() throws JsonProcessingException {

        Pageable pageable = PageRequest.of(0,
                6,
                Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPersonByName("ayr", pageable)
                .getContent().getFirst();

        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getId());
        Assertions.assertNotNull(person.getFirstName());
        Assertions.assertNotNull(person.getLastName());
        Assertions.assertNotNull(person.getAddress());
        Assertions.assertNotNull(person.getGender());

        assertEquals(1, person.getId());

        assertEquals("Ayrton", person.getFirstName());
        assertEquals("Senna", person.getLastName());
        assertEquals("São Paulo", person.getAddress());
        assertEquals("Male", person.getGender());
        assertTrue(person.getEnabled());
    }

    @Test
    @Order(1)
    public void testDisablePerson() throws JsonProcessingException {

        repository.disablePerson(person.getId());

        Pageable pageable = PageRequest.of(0,
                6,
                Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPersonByName("ayr", pageable)
                .getContent().getFirst();

        Assertions.assertNotNull(person);
        Assertions.assertNotNull(person.getId());
        Assertions.assertNotNull(person.getFirstName());
        Assertions.assertNotNull(person.getLastName());
        Assertions.assertNotNull(person.getAddress());
        Assertions.assertNotNull(person.getGender());

        assertEquals(1, person.getId());

        assertEquals("Ayrton", person.getFirstName());
        assertEquals("Senna", person.getLastName());
        assertEquals("São Paulo", person.getAddress());
        assertEquals("Male", person.getGender());
        assertFalse(person.getEnabled());
    }
}
