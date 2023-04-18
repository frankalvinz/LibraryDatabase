package ca.chibueze_ekwomadu.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ca.chibueze_ekwomadu.beans.Book;
import ca.chibueze_ekwomadu.beans.Message;
import ca.chibueze_ekwomadu.database.DatabaseAccess;
import lombok.AllArgsConstructor;

/**
 * This class is a REST controller class for Assignment4Application.java. 
 * This is specifically for REST API end points.
 * @author chibueze frank ekwomadu
 * November 15, 2022
 */
@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {
	private DatabaseAccess database;
	
	/**
	 * Returns a list of books to console of RESTful application.
	 * @return books a list of book object fetched from database
	 */
	@GetMapping
	public List<Book> getBooks() {
		List <Book> books = database.getBooks();
		return books;
	}
	
	/**
	 * If book isn't null, this method returns a book to console of RESTful application,
	 * else it returns an error message to console of RESTful application.
	 * @param id the id of the book to be affected
	 * @return ResponsEntity 
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getBook(@PathVariable Long id) {
		Book book = database.getBook(id);
		
		if (book != null) {
			return ResponseEntity.ok(book);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", "No Book with such record"));
		}
	}
	
	/**
	 * If book reviews isn't null, this method returns a book's reviews to console of 
	 * RESTful application, else it returns an error message to console of RESTful application.
	 * @param id the id of the book to be affected
	 * @return ResponsEntity 
	 */
	@GetMapping("/{id}/reviews")
	public ResponseEntity<?> getReview(@PathVariable Long id) {
		Book book = database.getBook(id);
		
		if (book != null) {
			return ResponseEntity.ok(book.getReviews());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", "No Review with such record"));
		}
	}
	
	/**
	 * Adds a new book object to books database.
	 * @param book object added to books database
	 * @return ResponseEntity
	 */
	@PostMapping(consumes="application/json")
	public ResponseEntity<?> postBook(@RequestBody Book book) {
		
		try {
			Long id = database.addBook(book);
			//sets book ID using keyLogger from addBook() method
			book.setId(id);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
			return ResponseEntity.created(location).body(book);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("error", "Title + Author already exists"));
		}
		
	}
}
