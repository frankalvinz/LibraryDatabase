package ca.chibueze_ekwomadu.security;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * This class handles illegal access to an unauthorized page by redirecting the user 
 * to '/permission-denied' page and logs the details of the attempt to the console
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Component
public class LoggingAccessDeniedHandler implements AccessDeniedHandler{
	

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.access.AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		//Get the user from the security context
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//"Log" the attempt to the console
		if (auth != null) {
			String format = "%s was trying to access %s\n";
			System.out.printf(format, auth.getName(), request.getRequestURI());
		}
		
		//redirect to the permission-denied page
		response.sendRedirect("/permission-denied");
	}
	
	
}
