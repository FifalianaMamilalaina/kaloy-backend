package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="audio_storage_types")
public class AudioStorageType  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@Column(name="name", unique = true, nullable = false)
	@NotNull(message = "Name cannot be null")
	@Size(max = 50)
	private String name;
	

}
