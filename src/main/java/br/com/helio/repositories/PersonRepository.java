package br.com.helio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.helio.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {}