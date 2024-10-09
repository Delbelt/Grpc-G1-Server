package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.OrderItem;

public interface IOrderItemRepository extends JpaRepository<OrderItem, Integer> {
	
	public OrderItem findByCode(String code);

}
