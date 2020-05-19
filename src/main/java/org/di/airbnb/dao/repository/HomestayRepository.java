package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomestayRepository extends JpaRepository<Property, Long> {}
