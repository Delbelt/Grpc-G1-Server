package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import server.entities.OrderItem;
import server.repositories.IOrderItemRepository;
import server.services.IOrderItemService;

@Service
public class OrderItemService implements IOrderItemService {
	
	@Autowired
	private IOrderItemRepository repository;

	@Override
	public OrderItem findByCode(String code) {
		
		return repository.findByCode(code);
	}
	
	public OrderItem findById(int id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public List<OrderItem> getAll() {
		
		return repository.findAll();
	}

	@Override
	public boolean insertOrUpdate(OrderItem order) {
		
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
}
