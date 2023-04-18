package ca.chibueze_ekwomadu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A SpringBoot application that utilizes LOMBOK library, SESSIONS, H2 DATABASE, SPRING SECURITY, 
 * WEB, THYMELEAF & RESTful API End Points to create a user-segmented web-application site for 
 * a library database. This site displays books and reviews of each book. A USER & an ADMIN 
 * role can add reviews but only an ADMIN role can add a book to the library database.
 * You can find the User Credentials on the Console once this application is running
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@SpringBootApplication
public class LibraryDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryDatabaseApplication.class, args);
	}

}
