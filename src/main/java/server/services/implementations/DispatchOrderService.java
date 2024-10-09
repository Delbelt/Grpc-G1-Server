package server.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import server.entities.DispatchOrder;
import server.entities.PurchaseOrder;
import server.repositories.IDispatchOrderRepository;
import server.repositories.IPurchaseOrderRepository;
import server.services.IDispatchOrderService;
import server.util.StatePurchaseOrder;

@Service
public class DispatchOrderService implements IDispatchOrderService {
	
	@Autowired
	private IDispatchOrderRepository repository;
	
	@Autowired
	private IPurchaseOrderRepository purchaseOrderRepository;

	@Override
	public DispatchOrder findByDispatchOrder(int dispatchOrder) {
		return repository.findById(dispatchOrder).orElse(null);
	}

	@Override
	public List<DispatchOrder> getAll() {
		
		return repository.findAll();
	}

	@Override
	@Transactional
	public boolean insertOrUpdate(DispatchOrder dispatchOrder, int idPurchaseOrder) {
		
		PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(idPurchaseOrder)
	            .orElseThrow(() -> new RuntimeException("PurchaseOrder not found"));
		
		 dispatchOrder.setIdPurchaseOrder(purchaseOrder);
		 dispatchOrder.setEstimatedDate(LocalDate.now());
		 
		 DispatchOrder savedDispatchOrder = repository.save(dispatchOrder);
		 
		 purchaseOrder.setDispatchOrder(savedDispatchOrder);
		 purchaseOrder.setState(StatePurchaseOrder.ACCEPTED);
		 
		 purchaseOrderRepository.save(purchaseOrder);
		 
		 return repository.save(dispatchOrder) != null;
	}

	@Override
	public boolean remove(int dispatchOrder) {
		boolean isDeleted = false;

		try {
			
			repository.deleteById(dispatchOrder);
			isDeleted = true;
		}

		catch (Exception e) {
			// TODO: handle exception
		}

		return isDeleted;
	}
	
	

}
