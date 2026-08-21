package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;



@Getter @Setter
public class SearchHistorySearch {

	private Long id;

	private User useridUsers;
	
	private String queryText;
	private java.time.LocalDateTime searchedAt;
	private java.time.LocalDateTime searchedAtMin;
	private java.time.LocalDateTime searchedAtMax;

	
}
