package com.example.demo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends CrudRepository<Booking, Long>{
	
	List<Booking> findByUsernameIgnoreCase(String username);

	@Query("select b from Booking b where b.flightId = :flightid and b.date >= :date and b.date < :date2")	
	List<Booking> findByFlightIdAndDate(@Param("flightid") Long flightId, @Param("date") Timestamp date , @Param("date2") Timestamp date2);
	
}