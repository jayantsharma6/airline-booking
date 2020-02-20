package com.example.demo;

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
public class UserCtr {
	
	@Autowired
	private UserRepository repository;
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	ResponseEntity<Object> getUser(@RequestParam("si") Optional<String> si){
		Iterable<User> res;
		if(si.isPresent())
			res = repository.findByUsername(si);
		else
			res = repository.findAll();
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	ResponseEntity<Object> getUserById(@PathVariable("id") Long id){
		Optional<User> res = repository.findById(id);
		if(res.isPresent()) {
			return new ResponseEntity<Object>(res, HttpStatus.OK);			
		}
		else
			return new ResponseEntity<Object>(new MyJSONWrapper("No such object"), HttpStatus.OK);			
	}
	
	@RequestMapping(value="/users", method = RequestMethod.POST)
	ResponseEntity<Object> insertUser(@RequestBody User u) {
		User res = repository.save(u);
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
	ResponseEntity<Object> updateUser(@RequestBody User u, @PathVariable("id") Long id){
		u.setUserId(id);
		User res = repository.save(u);
		return new ResponseEntity<Object>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	ResponseEntity<Object> deleteUser(@PathVariable("id") Long id){
		Optional<User> u = repository.findById(id);

		if(u.isPresent()) {
			repository.delete(u.get());
			return new ResponseEntity<Object>(new MyJSONWrapper("deleted"), HttpStatus.OK);			
		}
		else
			return new ResponseEntity<Object>(new MyJSONWrapper("Error","no such object."), HttpStatus.BAD_REQUEST);
	}

}
