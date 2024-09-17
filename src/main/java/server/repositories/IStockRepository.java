package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Stock;

public interface IStockRepository extends JpaRepository<Stock, Integer> {
	
}