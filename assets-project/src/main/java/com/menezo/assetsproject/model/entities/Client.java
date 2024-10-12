package com.menezo.assetsproject.model.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private List<Portfolio> portfolios;

    public Client() {
    }

    public Client(String name, List<Portfolio> portfolios) {
        this.name = name;
        this.portfolios = portfolios;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
