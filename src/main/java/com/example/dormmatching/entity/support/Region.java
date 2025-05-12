package com.example.dormmatching.entity.support;

import com.example.dormmatching.entity.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

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