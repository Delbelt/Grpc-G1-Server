package server.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="stock")
public class Stock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int IdStore;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@Column(name="codeProduct", nullable=false)
	private Product Product;
	
	@Column(name="quantity", nullable=false)
	private int Quantity;
	
}