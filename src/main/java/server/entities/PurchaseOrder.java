package server.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@Table(name="purchase_order")
@NoArgsConstructor
public class PurchaseOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idPurchaseOrder;
	
	@OneToOne(cascade=CascadeType.PERSIST, optional = true) 
	@JoinColumn(name="dispatchOrder", nullable = true)
	private DispatchOrder dispatchOrder;
	
	@ManyToOne(cascade=CascadeType.MERGE) 
	@JoinColumn(name="codeStore")
	private Store store;
	
	@Column(name="state", nullable=false)
	private String state;
	
	@Column(name="observations", nullable=true)
	private String observations;
	
	@Column(name="requestDate", nullable=false, columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime requestDate;
	
	@Column(name="ReceiptDate", nullable=true, columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime ReceiptDate;	
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name="idPurchaseOrder")
	private List<OrderItem> items;

	public PurchaseOrder(Store store, String state, LocalDateTime requestDate,String observations, List<OrderItem> items) {
		super();
		this.store = store;
		this.state = state;	
		this.requestDate = requestDate;
		this.items = items;
		this.observations = observations;
	}	
}
