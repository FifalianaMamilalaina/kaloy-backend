package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="instrument_roles")
public class InstrumentRole  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@Column(name="label", unique = true, nullable = false)
	@NotNull(message = "Label cannot be null")
	@Size(max = 50)
	private String label;
	

}
