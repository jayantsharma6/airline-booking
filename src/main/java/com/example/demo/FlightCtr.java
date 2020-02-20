package com.example.demo;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FlightCtr {
	
	@Autowired
	private FlightRepository repository;
	@Autowired
	private BookingRepository bookingrepository;
	
	@RequestMapping(value="/flights", method=RequestMethod.GET)
	ResponseEntity<Object> getFlight(@RequestParam("si") Optional<String> si){
		try {
		Iterable<Flight> res;
		if(si.isPresent())
			res = repository.findByFlightName(si);
		else
			res = repository.findAll();
		return new ResponseEntity<Object>(res, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}				
	}
	
	@RequestMapping(value="/flights/{id}", method=RequestMethod.GET)
	ResponseEntity<Object> getFlightById(@PathVariable("id") Long id){
		try {
		Optional<Flight> res = repository.findById(id);
		if(res.isPresent()) {
			return new ResponseEntity<Object>(res, HttpStatus.OK);			
		}
		else
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", "No such object"), HttpStatus.OK);			
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}
	
	
	@RequestMapping(value="/flights/{src}/to/{dest}", method=RequestMethod.GET)
	ResponseEntity<Object> getFlightBySourceAndDestination(@PathVariable("src") String source, @PathVariable("dest") String destination){
		try {
		List<Flight> res = repository.findBySourceAndDestinationAllIgnoreCase(source, destination);
		if(res.size()>0) {
			return new ResponseEntity<Object>(res, HttpStatus.OK);			
		}
		else {
			return new ResponseEntity<Object>(new MyJSONWrapper("No flight from " + source + " to " + destination), HttpStatus.OK);						
		}
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		

	}
	

	
	@RequestMapping(value="/flights/{flightid}/{date}", method=RequestMethod.GET)
	ResponseEntity<Object> getFlightDetailsForDate(@PathVariable("flightid") Long flightid, @PathVariable("date") String dateStr){
		try {
			if(dateStr==null || "".equals(dateStr))
				return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"Date can not be empty!"), HttpStatus.BAD_REQUEST);			
			
		Optional<Flight> res = repository.findById(flightid);
		if(res.isPresent()) {
			Flight f1 = res.get();	
			Timestamp date = MyUtils.getTimestamp(dateStr);		
			date.setHours(f1.getScheduledTime().getHours());	
			date.setMinutes(f1.getScheduledTime().getMinutes());	
			date.setSeconds(f1.getScheduledTime().getSeconds());
	    	Timestamp today = new Timestamp(new Date().getTime());
	        if(!date.after(today)) {
				return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Past date information not allowed!"), HttpStatus.BAD_REQUEST);						
			}			
			Timestamp date2 = MyUtils.getNextDay(date);
			List<Booking> bookings = bookingrepository.findByFlightIdAndDate(flightid, date, date2);
			int bookedSeats = 0;
			for(Booking b1 : bookings) {
				if("Booked".equalsIgnoreCase(b1.getStatus())) {
					bookedSeats += b1.getSeatsBooked();					
				}
			}
			String res2 = "Total seats = " + f1.getTotalSeats() + " , Available Seats = " + (f1.getTotalSeats()-bookedSeats);
			return new ResponseEntity<Object>(res2, HttpStatus.OK);									
		}else {
			return new ResponseEntity<Object>(new MyJSONWrapper("Error","No Flight Available!"), HttpStatus.BAD_REQUEST);						
		}
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}
	
	@RequestMapping(value="/flights", method = RequestMethod.POST)
	ResponseEntity<Object> insertUser(@RequestBody Flight u) {
		try {
		Flight res = repository.save(u);
		return new ResponseEntity<Object>(res, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
		
	}
	
	@RequestMapping(value = "/flights/{id}", method = RequestMethod.PUT)
	ResponseEntity<Object> updateUser(@RequestBody Flight u, @PathVariable("id") Long id){
		try {
		u.setFlightId(id);
		Flight res = repository.save(u);
		return new ResponseEntity<Object>(res, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}
	
	@RequestMapping(value = "/flights/{id}", method = RequestMethod.DELETE)
	ResponseEntity<Object> deleteUser(@PathVariable("id") Long id){
		try {
		Optional<Flight> u = repository.findById(id);

		if(u.isPresent()) {
			repository.delete(u.get());
			return new ResponseEntity<Object>(new MyJSONWrapper("deleted"), HttpStatus.OK);			
		}
		else
			return new ResponseEntity<Object>(new MyJSONWrapper("Error","no such object."), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}
}
