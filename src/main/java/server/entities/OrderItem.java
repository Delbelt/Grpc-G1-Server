package server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@Table(name="order_item")
@NoArgsConstructor
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idOrderItem;	

	@Column(name="code", nullable=false)
	private String code;
	
	@Column(name="color", nullable=false)
	private String color;
	
	@Column(name="size", nullable=false)
	private String size;
	
	@Column(name="quantity", nullable=false)
	private int quantity;

	public OrderItem(String code, String color, String size, int quantity) {
		super();
		this.code = code;
		this.color = color;
		this.size = size;
		this.quantity = quantity;
	}
	
	
}
