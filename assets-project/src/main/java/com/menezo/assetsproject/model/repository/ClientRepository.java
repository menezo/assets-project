package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);

    //Find by specific portfolio
    @Query("SELECT c FROM Client c JOIN c.portfolios p WHERE p.id = :portfolioId")
    List<Client> findByPortfolioId(Long portfolioId);

    @EntityGraph(attributePaths = {"portfolios.assets"})
    Optional<Client> findById(Long id);
}
