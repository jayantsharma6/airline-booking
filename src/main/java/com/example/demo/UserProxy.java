package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProxy {

	@Autowired
	private UserRepository repository;

	User validate(String username, String password) {
		User res = null;
		
		List<User> res2 = repository.findByUsernameIgnoreCaseAndPasswordIgnoreCase(username, password);
		if(res2.size()>0)
			res = res2.get(0);
		return res;
	}
	
	void insert(User u) {
		repository.save(u);
	}
	
	User getUser(String username, String email) {
		User res = null;		
		List<User> res2 = repository.findByUsernameIgnoreCaseAndEmailIgnoreCase(username, email);
		if(res2.size()>0)
			res = res2.get(0);
		return res;
	}

}
