package com.recognition.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recognition.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
  Optional<Users> findByUsername(String username);

  Optional<Users> findByEmail(String email);

  List<Users> findByFirstName(String firstName);

  List<Users> findByLastName(String lastName);
}
