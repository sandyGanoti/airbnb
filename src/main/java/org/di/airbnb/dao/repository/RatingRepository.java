package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {}
