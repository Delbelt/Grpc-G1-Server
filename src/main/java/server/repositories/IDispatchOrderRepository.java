package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.DispatchOrder;

public interface IDispatchOrderRepository extends JpaRepository<DispatchOrder, Integer> {	
	
}
