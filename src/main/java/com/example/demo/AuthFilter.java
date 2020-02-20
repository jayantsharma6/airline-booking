package com.example.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class AuthFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Fetching the authorization header from the request.
		String authenticationHeader= request.getHeader("authorization");

		try {
			SecurityContext context= SecurityContextHolder.getContext();

			if(authenticationHeader != null && authenticationHeader.startsWith(UserConst.BEARER_NAME)) {

				final String bearerTkn= authenticationHeader.replaceAll((UserConst.BEARER_NAME+ " "), "");
				System.out.println("Following token is received from the protected url= "+ bearerTkn);

				try {
					// Parsing the jwt token.
					Jws<Claims> claims = Jwts.parser().requireIssuer(UserConst.ISSUER_NAME).setSigningKey(UserConst.SECRET_KEY).parseClaimsJws(bearerTkn);

					// Obtaining the claims from the parsed jwt token.
					String user= (String) claims.getBody().get("usr");
					String roles= (String) claims.getBody().get("rol");

					// Creating the list of granted-authorities for the received roles.
					List<GrantedAuthority> authority= new ArrayList<GrantedAuthority>();
					for(String role: roles.split(","))
						authority.add(new SimpleGrantedAuthority("ROLE_"+role));
					
					System.out.println(authority);
//					User u1 = new User(user, "", authority);

					// Creating an authentication object using the claims.
					UsernamePasswordAuthenticationToken authenticationTkn= new UsernamePasswordAuthenticationToken(user, null, authority);					
					// Storing the authentication object in the security context.
					context.setAuthentication(authenticationTkn);
				} catch (SignatureException e) {
					throw new ServletException("Invalid token.");
				}
			}

			filterChain.doFilter(request, response);
			context.setAuthentication(null);
		} catch(AuthenticationException ex) {
			throw new ServletException("Authentication exception.");
		}
	}
}