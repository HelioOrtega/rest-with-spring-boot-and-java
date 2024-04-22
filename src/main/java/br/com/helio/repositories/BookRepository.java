package br.com.helio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.helio.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {}