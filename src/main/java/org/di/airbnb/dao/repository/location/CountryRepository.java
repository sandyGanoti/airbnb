package org.di.airbnb.dao.repository.location;

import org.di.airbnb.dao.entities.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {}
