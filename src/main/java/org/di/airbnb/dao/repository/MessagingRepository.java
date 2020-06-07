package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.Messaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagingRepository extends JpaRepository<Messaging, Long> {}
