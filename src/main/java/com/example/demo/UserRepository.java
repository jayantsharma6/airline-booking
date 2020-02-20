package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long>{

	List<User> findByUsername(Optional<String> si);
	List<User> findByUsernameIgnoreCaseAndEmailIgnoreCase(String username, String email);	
	List<User> findByUsernameIgnoreCaseAndPasswordIgnoreCase(String username, String password);

}
