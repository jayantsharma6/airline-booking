package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface FlightRepository extends CrudRepository<Flight, Long>{
	
	List<Flight> findByFlightName(Optional<String> si);

	List<Flight> findBySourceAndDestinationAllIgnoreCase(String source, String destination);
	
}
