package org.lemanoman.filesyncserver.model;

import jakarta.persistence.*;

import java.util.Base64;

@Entity
@Table(name = "location")
public class LocationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "location_generator", sequenceName = "location_seq", allocationSize = 1)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String pathkey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathkey() {
        return pathkey;
    }

    public void setPathkey(String pathkey) {
        this.pathkey = pathkey;
    }
}
