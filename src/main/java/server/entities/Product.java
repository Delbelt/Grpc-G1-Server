package server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
	
	@Lob
	@Column(name = "photo", columnDefinition = "LONGBLOB", nullable = true)
    private byte[] photo;
	
	@Column(name="color", nullable=false)
	private String color;
	
	@Column(name="active", nullable=false)
	private boolean active;
}