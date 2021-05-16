package com.example.shopifychallenge.repositories;

import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.models.User;
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
