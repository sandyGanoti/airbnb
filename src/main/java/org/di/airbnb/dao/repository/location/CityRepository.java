package org.di.airbnb.dao.repository.location;

import org.di.airbnb.dao.entities.location.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {}
