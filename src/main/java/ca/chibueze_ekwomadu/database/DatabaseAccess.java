package ca.chibueze_ekwomadu.database;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ca.chibueze_ekwomadu.beans.Book;
import ca.chibueze_ekwomadu.beans.Review;
import lombok.AllArgsConstructor;

/**
 * This class acts as a repository to the h2Database
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Repository
@AllArgsConstructor
public class DatabaseAccess {
	private NamedParameterJdbcTemplate jdbc;
	
	/**
	 * Gets all books available from the books database 
	 * @return list of all books gotten from the database
	 */
	public List<Book> getBooks() {
		//Injects SQL statements to view books from database
		String query = "SELECT * FROM books";
		//will map a row coming in to an instance of Book
		BeanPropertyRowMapper <Book> bookMapper = new BeanPropertyRowMapper<Book>(Book.class);
		//
		List <Book> books = jdbc.query(query, bookMapper);
		//sets reviews for each book
		for(Book book : books) {
			book.setReviews(getReviews(book.getId()));
		}
		return books;
	}
	
	/**
	 * Adds a book to books database
	 * @param book a book object that is created and added to the database
	 * @return he number of rows affected; 1 - successful, 0 - not successful
	 */
	public Long addBook(Book book) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		//Injects SQL statements to insert new instance into table
		String query = "INSERT INTO books (title, author) "
				+ "VALUES (:title, :author)";
		//adding the parameters to my map
		params
			.addValue("title", book.getTitle())
			.addValue("author", book.getAuthor());
		
		//this part is specifically for RESTfull additions via POSTman, 
		//switch to 'returnvalue' as @return value when not in use.
		KeyHolder key = new GeneratedKeyHolder();
		int returnValue = jdbc.update(query, params, key);
		Long id = (Long) key.getKey();
		return ((returnValue > 0) ? id: 0);
	}
	
	/**
	 * Adds a review to reviews database
	 * @param review a review object that is created and added to the database
	 * @return he number of rows affected; 1 - successful, 0 - not successful
	 */
	public int addReview(Review review) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		//Injects SQL statements to insert new instance into table
		String query = "INSERT INTO reviews (bookId, text) "
				+ "VALUES (:bookId, :text)";
		//adding the parameters to my map
		params
			.addValue("bookId", review.getBookId())
			.addValue("text", review.getText());
		//
		int returnValue = jdbc.update(query, params);
		return returnValue;
	}
	
	/**
	 * Gets a book with id 'id' from missions database
	 * @param id the id of the book to be affected
	 * @return the number of rows affected; 1 - successful, 0 - not successful
	 */
	public Book getBook(Long id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String query = "SELECT * FROM books WHERE id = :id";
		//add the parameters to map
		params.addValue("id", id);
		//will map a row to an instance of Book
		BeanPropertyRowMapper<Book> mapper = new BeanPropertyRowMapper <> (Book.class);
		//declare and initialize to null
		Book book = null;
		try {
			//Use the queryForObject method to get the one instance
			book = jdbc.queryForObject(query, params, mapper);
			book.setReviews(getReviews(book.getId()));
		} catch(EmptyResultDataAccessException ex) {
			//if there is no match, print exception to console
			System.out.println("Book not found for id" + id);
		}
		return book;
	}
	
	/**
	 * Gets all reviews with bookId "id" from the reviews database 
	 * @return list of all reviews gotten from the database
	 */
	public List<Review> getReviews(Long id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		String query = "SELECT * FROM reviews WHERE bookId = :id";
		//add the parameters to map
		params.addValue("id", id);
		//will map a row coming in to an instance of Review
		BeanPropertyRowMapper <Review> reviewMapper = new BeanPropertyRowMapper<Review>(Review.class);
		//
		List <Review> reviews = jdbc.query(query, params, reviewMapper);
		return reviews;
	}
	
	
}