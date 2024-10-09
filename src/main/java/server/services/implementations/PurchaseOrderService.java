package server.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import server.entities.PurchaseOrder;
import server.repositories.IPurchaseOrderRepository;
import server.services.IPurchaseOrderService;

@Service
public class PurchaseOrderService implements IPurchaseOrderService {
	
	@Autowired
	private IPurchaseOrderRepository repository;

	@Override
	public PurchaseOrder findById(int id) {
			
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<PurchaseOrder> getAll() {
		
		return repository.getAllRelationship();
	}
	
	@Override
	public List<PurchaseOrder> getAllByDate(LocalDate date) {
		
		return repository.getAllRelationshipFromDate(date);
	}
	
	public List<PurchaseOrder> getAllFromState(String state) {
		return repository.getAllFromState(state);
	}

	@Override
	public PurchaseOrder getByIdRelationship(int id) {
		
		return repository.findByDispatchOrderRelationship(id);
	}
	
	@Override
	public PurchaseOrder insert(PurchaseOrder order) {
	
		return repository.save(order);
	}

	@Override
	public boolean insertOrUpdate(PurchaseOrder order, String state) {
		
		order.setState(state);
		
		return repository.save(order) != null ? true : false;
	}
	
	@Override
	public boolean Update(PurchaseOrder order) {
		
		return repository.save(order) != null ? true : false;
	}

	@Override
	public boolean remove(int id) {
		
		boolean isDeleted = false;

		try {
			
			repository.deleteById(id);
			isDeleted = true;
		}

		catch (Exception e) {
			// TODO: handle exception
		}

		return isDeleted;
	}

	@Override
	public List<PurchaseOrder> getAllByCodeItem(LocalDate date) {
		// TODO Auto-generated method stub
		return null;
	}
}
