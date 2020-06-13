package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.RentingRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentingRulesRepository extends JpaRepository<RentingRules, Long> {

}
