package server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity // Para que se considere entidad
@Table(name="product")
public class Product {

	@Id
	private String code;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="size", nullable=false)
	private String size;	
	
	@Column(name = "photo", nullable = false)
    private String photo;
	
	@Column(name="color", nullable=false)
	private String color;
	
	@Column(name="active", nullable=false)
	private boolean active;
}