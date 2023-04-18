package ca.chibueze_ekwomadu.beans;

import lombok.Data;

/**
 * A POJO class of 'Review'
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Data
public class Review {
	
	private Long id;
	private Long bookId;
	private String text;
	
}
