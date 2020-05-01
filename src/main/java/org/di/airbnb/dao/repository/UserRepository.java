package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
