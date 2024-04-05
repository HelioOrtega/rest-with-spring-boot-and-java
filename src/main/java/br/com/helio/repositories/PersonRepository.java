package br.com.helio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.helio.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {}