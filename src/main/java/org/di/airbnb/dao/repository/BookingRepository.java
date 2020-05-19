package org.di.airbnb.dao.repository;

import org.di.airbnb.dao.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {}
