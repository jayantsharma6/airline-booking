package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class UserConfig extends WebSecurityConfigurerAdapter{

	@Bean
	public AuthFilter AuthenticationFilter() {
		return new AuthFilter();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic()
			.and()
			.authorizeRequests()
			.antMatchers("/login")
			.permitAll()
			.antMatchers("/signup")
			.permitAll()
			.and()
			.authorizeRequests()
	        .antMatchers(HttpMethod.GET, "/users").hasRole("USER")
	        .antMatchers(HttpMethod.GET, "/users/**").hasRole("USER")
	        .antMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
	        .antMatchers(HttpMethod.POST, "/users/").hasRole("ADMIN")
	        .antMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.GET, "/flights").hasRole("USER")
	        .antMatchers(HttpMethod.GET, "/flights/**").hasRole("USER")
	        .antMatchers(HttpMethod.POST, "/flights").hasRole("ADMIN")
	        .antMatchers(HttpMethod.POST, "/flights/").hasRole("ADMIN")
	        .antMatchers(HttpMethod.PUT, "/flights/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.DELETE, "/flights/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.GET, "/bookings").hasRole("USER")
	        .antMatchers(HttpMethod.GET, "/bookings/**").hasRole("USER")
	        .antMatchers(HttpMethod.GET, "/mybookings/**").hasRole("USER")
	        .antMatchers(HttpMethod.POST, "/bookings").hasRole("ADMIN")
	        .antMatchers(HttpMethod.POST, "/bookings/").hasRole("ADMIN")
	        .antMatchers(HttpMethod.POST, "/bookings/").hasRole("USER")
	        .antMatchers(HttpMethod.PUT, "/bookings/**").hasRole("USER")
	        .antMatchers(HttpMethod.DELETE, "/bookings/**").hasRole("ADMIN")
	        .and()
			.csrf().disable()
			.addFilterBefore(AuthenticationFilter(), 
					UsernamePasswordAuthenticationFilter.class);
			
	}

}
