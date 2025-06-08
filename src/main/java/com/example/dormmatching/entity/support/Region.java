package com.example.dormmatching.entity.support;

import com.example.dormmatching.entity.user.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@JsonIgnoreProperties({ "users", "hibernateLazyInitializer", "handler" })
@Entity
@Setter
@Getter
@Table(name = "regions")
public class Region {
    @Id
    @Column(name = "region_id")
    private Integer regionId;

    @Column(length = 50)
    private String regionName;

    @Column(nullable = false)
    private Integer distanceScore;

    @OneToMany(mappedBy = "region")
    private Set<User> users;

    // getters and setters...
}