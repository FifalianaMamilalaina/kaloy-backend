package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="search_history")
public class SearchHistory  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@Column(name="query_text", unique = false, nullable = false)
	@NotNull(message = "QueryText cannot be null")
	@Size(max = 255)
	private String queryText;
	@Column(name="searched_at", unique = false, nullable = false)
	@NotNull(message = "SearchedAt cannot be null")
	private java.time.LocalDateTime searchedAt;
	

}
