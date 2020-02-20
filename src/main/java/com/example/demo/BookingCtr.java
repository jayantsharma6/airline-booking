package com.example.demo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingCtr {
	
	@Autowired
	private BookingRepository repository;
	@Autowired
	private FlightRepository flightrepository;
	
	@RequestMapping(value="/bookings/{id}", method=RequestMethod.GET)
	ResponseEntity<Object> getBookingById(@PathVariable("id") Long id){
		try {
			Optional<Booking> res = repository.findById(id);
			if(res.isPresent()) {
				return new ResponseEntity<Object>(res, HttpStatus.OK);			
			}
			else
				return new ResponseEntity<Object>( new MyJSONWrapper("Error", "No such object"), HttpStatus.OK);						
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}
	}
	
	@RequestMapping(value="/mybookings/future", method=RequestMethod.GET)
	ResponseEntity<Object> getBookingByUsername(){
		try {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getPrincipal().toString();
		List<Booking> res = repository.findByUsernameIgnoreCase(username);
//		return new ResponseEntity<Object>(res, HttpStatus.OK);			
    	Timestamp today = MyUtils.getTodayWithoutTime();
    	List<Booking> filterRes = new LinkedList<Booking>();
    	for(Booking b1 : res) {
    		if(b1.getDate().after(today)) {
    			filterRes.add(b1);
    		}
    	}
    	if(filterRes.size()>0) {
    		return new ResponseEntity<Object>(filterRes, HttpStatus.OK);			    		    		
    	}
    	else {
    		return new ResponseEntity<Object>(new MyJSONWrapper("Error", "No bookings for " + username), HttpStatus.OK);			    		
    	}
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}    	
	}
	
	@RequestMapping(value="/mybookings/past", method=RequestMethod.GET)
	ResponseEntity<Object> getBookingByUsernamePast(){
		try {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getPrincipal().toString();
		List<Booking> res = repository.findByUsernameIgnoreCase(username);
//		return new ResponseEntity<Object>(res, HttpStatus.OK);			
    	Timestamp today = MyUtils.getTodayWithoutTime();
    	List<Booking> filterRes = new LinkedList<Booking>();
    	for(Booking b1 : res) {
    		if(b1.getDate().before(today)) {
    			filterRes.add(b1);
    		}
    	}
    	if(filterRes.size()>0) {
    		return new ResponseEntity<Object>(filterRes, HttpStatus.OK);			    		    		
    	}
    	else {
    		return new ResponseEntity<Object>(new MyJSONWrapper("Error", "No bookings for " + username), HttpStatus.OK);			    		
    	}
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}    	
	}

	
//	@RequestMapping(value="/bookings", method = RequestMethod.POST)
//	ResponseEntity<Object> insertBooking(@RequestBody Booking b) {
//		Booking res = repository.save(b);
//		return new ResponseEntity<Object>(res, HttpStatus.OK);
//	}
	
	@RequestMapping(value="/bookings/flightId/{fid}", method = RequestMethod.POST)
	ResponseEntity<Object> bookTickets(@RequestBody Booking b, @PathVariable("fid") Long flightId) {
		try {
			if(b.getDate()==null)
				return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"Date can not be empty!"), HttpStatus.BAD_REQUEST);
			if(b.getSeatsBooked()<=0)
				return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"seatsBooked should be positive no!"), HttpStatus.BAD_REQUEST);

		Optional<Flight> res = flightrepository.findById(flightId);
		if(res.isPresent()) {
			Flight f1 = res.get();
			
			
			Timestamp date = MyUtils.getDateOnly(b.getDate());
			Timestamp date2 = MyUtils.getNextDay(date);
			List<Booking> bookings = repository.findByFlightIdAndDate(flightId, date, date2);
			int bookedSeats = 0;
			for(Booking b1 : bookings) {
				if("Booked".equalsIgnoreCase(b1.getStatus())) {									
					bookedSeats += b1.getSeatsBooked();
				}
			}
			if(bookedSeats+b.getSeatsBooked() > f1.getTotalSeats()) {
				return new ResponseEntity<Object>(new MyJSONWrapper("Available Seats = " +  (f1.getTotalSeats()-bookedSeats)), HttpStatus.BAD_REQUEST);										
			}			
			b.setFlightId(flightId);		
			if(b.getSeatsBooked()<=0) {
				b.setSeatsBooked(1);
			}
			b.setStatus("Booked");
			b.setTotalPrice(f1.getBasePrice()*b.getSeatsBooked());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			b.setUsername(auth.getPrincipal().toString());
			
	    	Timestamp today = new Timestamp(new Date().getTime());
	    	b.getDate().setHours(f1.getScheduledTime().getHours());
	    	b.getDate().setMinutes(f1.getScheduledTime().getMinutes());
	    	b.getDate().setSeconds(f1.getScheduledTime().getSeconds());
	    	System.out.println(b.getDate());
	    	System.out.println(today);
	        if(b.getDate().after(today)) {
				Booking res2 = repository.save(b);
				return new ResponseEntity<Object>(res2, HttpStatus.OK);				
	        } else {
				return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Past date booking not allowed!"), HttpStatus.BAD_REQUEST);						
			}
		}else {
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", "No Flight Available!"), HttpStatus.BAD_REQUEST);						
		}			
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}
	
	@RequestMapping(value = "/bookings/{id}/cancel", method = RequestMethod.PUT)
	ResponseEntity<Object> updateBooking(@PathVariable("id") Long id){
		try {
		Optional<Booking> res = repository.findById(id);
		if(res.isPresent()) {
			Booking b1 = res.get();
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getPrincipal().toString();
			if(username.equalsIgnoreCase(b1.getUsername())) {
		    	Timestamp tomorrow = MyUtils.getTomorrowWithoutTime();
		    	if(!"Booked".equalsIgnoreCase(b1.getStatus())) {
					return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Already Canceled!"), HttpStatus.BAD_REQUEST);						
		    	}
		    	if(b1.getDate().after(tomorrow)) {
		    		b1.setStatus("Canceled");
		    		Booking res2 = repository.save(b1);
		    		return new ResponseEntity<Object>(res2, HttpStatus.OK);
		    	}
		    	else {
					return new ResponseEntity<Object>(new MyJSONWrapper("You can not cancel past/today's flight!"), HttpStatus.BAD_REQUEST);			
		    	}
			}
			else {
				return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Not Authorized to Cancel"), HttpStatus.UNAUTHORIZED);			
			}				
		}
		else
			return new ResponseEntity<Object>("No Flights Found", HttpStatus.BAD_REQUEST);			
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
		}

//		u.setBookingId(id);
//		Booking res = repository.save(u);
//		return new ResponseEntity<Object>(res, HttpStatus.OK);
//	}

//	
//	@RequestMapping(value = "/bookings/{id}", method = RequestMethod.PUT)
//	ResponseEntity<Object> updateBooking(@RequestBody Booking u, @PathVariable("id") Long id){
//		u.setBookingId(id);
//		Booking res = repository.save(u);
//		return new ResponseEntity<Object>(res, HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/bookings/{id}", method = RequestMethod.DELETE)
	ResponseEntity<Object> deleteBooking(@PathVariable("id") Long id){
		try {
		Optional<Booking> u = repository.findById(id);

		if(u.isPresent()) {
			repository.delete(u.get());
			return new ResponseEntity<Object>("deleted", HttpStatus.OK);			
		}
		else
			return new ResponseEntity<Object>("no such object.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>( new MyJSONWrapper("Error", e.getMessage()), HttpStatus.BAD_REQUEST);						
		}		
	}


}
