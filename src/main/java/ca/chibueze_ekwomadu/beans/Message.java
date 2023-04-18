package ca.chibueze_ekwomadu.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A POJO class of 'Message'. This is specifically for RESTful services
 * @author chibueze frank ekwomadu
 * November 30, 2022
 */
@Data
@AllArgsConstructor
public class Message {
	
	 private String status;
	 private String message;
}
