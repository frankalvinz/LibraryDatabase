package ca.chibueze_ekwomadu.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.chibueze_ekwomadu.beans.Book;
import ca.chibueze_ekwomadu.beans.Review;
import ca.chibueze_ekwomadu.database.DatabaseAccess;
import lombok.AllArgsConstructor;

/**
 * This class is the main controller class for Assignment4Application.java
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Controller
@AllArgsConstructor
public class HomeController {
	private DatabaseAccess database;
	private BCryptPasswordEncoder encoder;
	private JdbcUserDetailsManager manager;
	
	/**
	 * Root page. It also prints default users info to console
	 * @return index.html
	 */
	@GetMapping("/")
	public String goHome(Model model) {
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		System.out.println("----------------- Default users and roles ------------------");
		System.out.println("|     Username: bugs, Password: bunny, Role(s): USER       |");
		System.out.println("|  Username: daffy, Password: duck, Role(s): USER, ADMIN   |");
		System.out.println("------------------------------------------------------------");
		return "index.html";
	}
	
	/**
	 * Takes user to the register page
	 * @return /register-page
	 */
	@GetMapping("/register-page")
	public String goToRegister() {
		return "/register-page";
	}
	
	/**
	 * Creates a new user. Takes in user's input, encodes the password, assigns the 'USER' role,
	 * and creates new user using the spring objects - (User, & JdbcUserDetailsManager).
	 * @return /index
	 */
	@PostMapping("/register")
	public String register(@RequestParam String username, @RequestParam String password, Model model) {
		//Role to be assigned
		List <GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("ROLE_USER"));
		//encode user password for protection
		String encodedPassword = encoder.encode(password);
		//create spring security user
		User newUser = new User(username, encodedPassword, roles);
		manager.createUser(newUser);
		//
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		model.addAttribute("message", "Thanks for registering. You can now Log in");
		return "/index";
	}
	
	/**
	 * Takes user to the add book page (ADMIN role authority only)
	 * @param model
	 * @return /secured/admin/add-book-page
	 */
	@GetMapping("/admin/add-book-page")
	public String goToAddBook(Model model) {
		model.addAttribute("book", new Book());
		return "/secured/admin/add-book-page";
	}
	
	/**
	 * Adds a book to books database (ADMIN role authority only)
	 * @param book a book object that is created and will be added to the database
	 * @param model
	 * @return /secured/admin/index
	 */
	@PostMapping("/admin/add-book")
	public String addBook(@ModelAttribute Book book, Model model) {
		Long returnValue = database.addBook(book);
		System.out.println("return value is: " + returnValue);
		//
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		return "/secured/admin/index";
	}
	
	/**
	 * Takes user to the add review page (USER OR ADMIN role authority)
	 * @param model
	 * @return /secured/user/add-review-page
	 */
	@GetMapping("/user/add-review-page/{id}")
	public String goToAddReview(@PathVariable Long id, Model model) {
		Book book = database.getBook(id);
		model.addAttribute("book", book);
		return "/secured/user/add-review-page";
	}
	
	/**
	 * Adds a book to books database (ADMIN role authority only)
	 * @param text a parameter of new review that is created
	 * @param bookId a parameter of new review that is created
	 * @param model
	 * @return /secured/user/reviews
	 */
	@PostMapping("/user/add-review")
	public String addReview(@RequestParam String text, @RequestParam Long bookId, Model model) {
		Review review = new Review();
		review.setBookId(bookId);
		review.setText(text);
		int returnValue = database.addReview(review);
		System.out.println("return value is: " + returnValue);
		//
		List<Review> reviews = database.getReviews(review.getBookId());
		Book book = database.getBook(review.getBookId());
		model.addAttribute("reviews", reviews);
		model.addAttribute("book", book);
		return "/secured/user/reviews";
	}
	
	/**
	 * Fetches selected reviews from the database using 'id'
	 * @param id is the ID number of book which will be used to fetch the reviews
	 * @param model adds the fetched book to thymeleaf as 'book'.
	 * @return /reviews
	 */
	@GetMapping("/books/reviews/{id}")
	public String viewReviews(@PathVariable Long id, Model model) {
		//Given the id, get the corresponding Reviews from the database
		List<Review> reviews = database.getReviews(id);
		//Given the id, get the corresponding Book from the database
		Book book = database.getBook(id);
		//Error condition for 'ID NOT FOUND' - prints id to console before returning to index
		if (reviews == null) {
			System.out.println("No result for id= " + id);
			return "/index";
		}
		model.addAttribute("reviews", reviews);
		model.addAttribute("book", book);
		return "/reviews";
	}
	
	/**
	 * Fetches selected reviews from the database using 'id' (ADMIN or USER role authority only)
	 * @param id is the ID number of book which will be used to fetch the reviews
	 * @param model adds the fetched book and reviews to thymeleaf as 'book' and 'reviews'.
	 * @return /secured/admin/reviews
	 */
	@GetMapping("/user/books/reviews/{id}")
	public String viewUserReviews(@PathVariable Long id, Model model) {
		//Given the id, get the corresponding Reviews from the database
		List<Review> reviews = database.getReviews(id);
		//Given the id, get the corresponding Book from the database
		Book book = database.getBook(id);
		//Error condition for 'ID NOT FOUND' - prints id to console before returning to index
		if (reviews == null) {
			System.out.println("No result for id= " + id);
			return "/index";
		}
		//
		model.addAttribute("reviews", reviews);
		model.addAttribute("book", book);
		return "/secured/user/reviews";
	}
	
	/**
	 * User root page (USER role authority only)
	 * @param model adds fetched books to thymeleaf as 'books'
	 * @return /secured/user/index
	 */
	@GetMapping("/user")
	public String goToUserSecured(Model model) {
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		return "/secured/user/index";
	}
	
	/**
	 * ADMIN root page (ADMIN role authority only)
	 * @param model adds fetched books to thymeleaf as 'books'
	 * @return /secured/admin/index
	 */
	@GetMapping("/admin")
	public String goToAdminSecured(Model model) {
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		return "/secured/admin/index";
	}
	
	/**
	 * Log in page
	 * @return login.html
	 */
	@GetMapping("/login")
	public String login() {
		return "login.html";
	}
	
	/**
	 * An error page. A USER or ADMIN is redirected here when illegally trying to access a page
	 * that they are not granted access to.
	 * @return /error/permission-denied
	 */
	@GetMapping("/permission-denied")
	public String goToDenied() {
		return "/error/permission-denied";
	}
}
