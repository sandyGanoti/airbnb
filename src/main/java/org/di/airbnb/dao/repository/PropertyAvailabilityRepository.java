package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.PropertyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyAvailabilityRepository extends JpaRepository<PropertyAvailability, Long> {}
