package server.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import server.entities.PurchaseOrder;

public interface IPurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
	
	@Query("FROM PurchaseOrder po inner join fetch po.items where po.idPurchaseOrder=(:id)")
	public PurchaseOrder findByDispatchOrderRelationship(int id);
	
	@Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items")
    List<PurchaseOrder> getAllRelationship();
	
	@Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE FUNCTION('DATE', po.requestDate) = :requestDate")
    List<PurchaseOrder> getAllRelationshipFromDate(LocalDate requestDate);
	
	@Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.state = :state")
    List<PurchaseOrder> getAllFromState(String state);
}
