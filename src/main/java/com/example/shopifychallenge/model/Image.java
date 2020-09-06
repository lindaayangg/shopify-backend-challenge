package com.example.shopifychallenge.model;

import com.example.shopifychallenge.utils.Permission;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnoreProperties("imageList")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "name")
    private String name;

    @Column(name = "permission")
    private Permission permission;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "amount")
    private Integer amount;
}
