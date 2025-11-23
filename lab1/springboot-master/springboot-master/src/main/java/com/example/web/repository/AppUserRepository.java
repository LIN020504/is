package com.example.web.repository;

import com.example.web.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    List<AppUser> findByBirth(LocalDateTime birth);
    List<AppUser> findByNameContaining(String name);
    long countByHairColor(String hairColor);
    long countByEyeColor(String eyeColor);

}
