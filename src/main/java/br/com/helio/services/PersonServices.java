package br.com.helio.services;

import br.com.helio.exceptions.ResourceNotFoundException;
import br.com.helio.model.Person;
import br.com.helio.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonServices {

    @Autowired
    PersonRepository repository;

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    public List<Person> findAll() {

        logger.info("Finding all people");

        return repository.findAll();
    }

    public Person findById(Long id) {
        logger.info("Finding one person");

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No records found for this Id " + id));
    }

    public Person create(Person person) {
        logger.info("Creating one person");

        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Updating one person");

        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No records found for this Id " + person.getId()));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return repository.save(person);
    }

    public void delete(Long id) {

        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No records found for this Id " + id));

        logger.info("Deleting one person");

        repository.delete(entity);
    }

}
