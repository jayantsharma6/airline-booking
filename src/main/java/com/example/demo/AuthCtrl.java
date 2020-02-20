package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class AuthCtrl {
	
	@Autowired
	UserProxy proxy;
	
	@Autowired
    private JavaMailSender javaMailSender;

	@PostMapping("/login")
	public ResponseEntity<Object> getToken(@RequestBody User login) throws ServletException {
		String jwttoken = "";
		if(login.getUsername().isEmpty() || login.getPassword().isEmpty())
			return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"Username or password cannot be empty."), HttpStatus.BAD_REQUEST);
		String name = login.getUsername(), password = login.getPassword();
		User u2 = proxy.validate(name, password);
		if(u2==null)
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Incorrect username and password."), HttpStatus.UNAUTHORIZED);
		else {
			// Creating JWT using the user credentials.
			Map<String, Object> claims = new HashMap<String, Object>();
			claims.put("usr", u2.getUsername());
			claims.put("sub", "Authentication token");
			claims.put("iss", UserConst.ISSUER_NAME);
			claims.put("rol", u2.getRole());
			claims.put("iat", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			jwttoken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, UserConst.SECRET_KEY).compact();
			System.out.println("Returning the following token to the user= "+ jwttoken);
		}
		return new ResponseEntity<Object>(new MyJSONWrapper(jwttoken), HttpStatus.OK);
	}
	
	
	@PostMapping("/forgotpass")
	public ResponseEntity<Object> forgotPass(@RequestBody User login) throws ServletException {
		if(login.getUsername() == null  || login.getUsername().isEmpty())
			return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"Username cannot be empty."), HttpStatus.BAD_REQUEST);
		if(login.getEmail() == null  || login.getEmail().isEmpty())
			return new ResponseEntity<Object>(new MyJSONWrapper("Error" ,"EMail cannot be empty."), HttpStatus.BAD_REQUEST);

		User u2 = proxy.getUser(login.getUsername(), login.getEmail());
		if(u2==null)
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Incorrect username or email."), HttpStatus.BAD_REQUEST);
		else {
			SimpleMailMessage msg = new SimpleMailMessage();
	        msg.setTo(login.getEmail());

	        msg.setSubject("Your password!");
	        msg.setText("Your password is " + u2.getPassword());
	        javaMailSender.send(msg);

		}
		return new ResponseEntity<Object>(new MyJSONWrapper("Please check your email!"), HttpStatus.OK);
	}

	
	@PostMapping("/signup")
	public ResponseEntity<Object> createUser(@RequestBody User login) throws ServletException {
		String err = MyUtils.validatePass(login.getPassword());
		if(err!=null)
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", err), HttpStatus.BAD_REQUEST);
		err = MyUtils.validateName(login.getUsername());
		if(err!=null)
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", err), HttpStatus.BAD_REQUEST);
		login.setRole("USER");
		
		try {
			proxy.insert(login);
			return new ResponseEntity<Object>(new MyJSONWrapper("Created"), HttpStatus.OK);			
		} catch (ConstraintViolationException e) {		
			MyJSONWrapper tmp = MyUtils.getJSONWraperFromExcep(e);
			return new ResponseEntity<Object>(tmp, HttpStatus.BAD_REQUEST);
//		} catch (DataIntegrityViolationException e) {		
//			return new ResponseEntity<Object>(tmp, HttpStatus.BAD_REQUEST);
		}		
		catch (Exception e) {
//			e.printStackTrace();
			return new ResponseEntity<Object>(new MyJSONWrapper("Error", "Could not create! :: " + e.getMessage()) , HttpStatus.BAD_REQUEST);
		}
	}

}