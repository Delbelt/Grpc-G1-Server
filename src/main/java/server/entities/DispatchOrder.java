package server.entities;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data 
@Table(name="dispatch_order")
public class DispatchOrder {	
	
	@Id
	@Column(name="dispatchOrder")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dispatchOrder;	
	
	@OneToOne(cascade=CascadeType.PERSIST) 
	@JoinColumn(name="idPurchaseOrder")
	private PurchaseOrder idPurchaseOrder;
	
	@Column(name="estimatedDate", nullable=true, columnDefinition = "DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate estimatedDate;
}
