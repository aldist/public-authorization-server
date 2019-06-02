package de.aldist.authrorizationserver.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUsersRepository extends JpaRepository<Users, Integer> {

  boolean existsByUsername(String username);

  Users findByUsername(String username);

  /*
    @Transactional
    void deleteByUsername(String username);
  */
}
