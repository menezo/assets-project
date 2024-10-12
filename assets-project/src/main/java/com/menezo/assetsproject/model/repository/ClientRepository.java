package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);
    @Query("SELECT c FROM Client c JOIN FETCH c.portfolios")
    List<Client> findAllWithPortfolios();

    //Find by specific portfolio
    @Query("SELECT c FROM Client c JOIN c.portfolios p WHERE p.id = :portfolioId")
    List<Client> findByPortfolioId(Long portfolioId);

}
