package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="venues")
public class Venue  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@Column(name="name", unique = false, nullable = false)
	@NotNull(message = "Name cannot be null")
	@Size(max = 150)
	private String name;
	@Column(name="location", unique = false, nullable = true)
	@Size(max = 255)
	private String location;
	

}
