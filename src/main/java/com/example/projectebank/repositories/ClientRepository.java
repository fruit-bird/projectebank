package com.example.projectebank.repositories;

import com.example.projectebank.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE c.name LIKE %?1%")
    List<Client> findByNameContaining(String keyword);
}
