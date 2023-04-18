package ca.chibueze_ekwomadu.beans;

import java.util.List;

import lombok.Data;

/**
 * A POJO class of 'Book'
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Data
public class Book {
	private Long id;
	private String title;
	private String author;
	
	private List<Review> reviews;
}
