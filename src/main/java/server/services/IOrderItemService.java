package server.services;

import java.util.List;

import server.entities.OrderItem;

public interface IOrderItemService {

	public OrderItem findByCode(String code);
	
	public OrderItem findById(int id);

	public List<OrderItem> getAll();

	public boolean insertOrUpdate(OrderItem order);

	public boolean remove(int id);
}
