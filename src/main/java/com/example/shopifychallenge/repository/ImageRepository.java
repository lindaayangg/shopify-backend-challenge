package com.example.shopifychallenge.repository;

import com.example.shopifychallenge.model.Image;
import com.example.shopifychallenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Image i WHERE i.owner = ?1")
    void deleteAllByOwner(User owner);
}
