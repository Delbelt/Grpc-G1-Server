package server.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="stock")
public class Stock {
	
	@Id
	@Column(name="code", nullable=false)
    private String code;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(name="codeStore")
	private Store store;
	
	@ManyToOne(cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name="codeProduct")
	private Product product;
	
	@Column(name="quantity", nullable=false)
	private int quantity;

	@Override
	public String toString() {
		return "Stock [code=" + code + ", quantity=" + quantity + "]";
	}	
}