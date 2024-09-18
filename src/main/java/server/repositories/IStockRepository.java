package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Product;
import server.entities.Stock;

public interface IStockRepository extends JpaRepository<Stock, Integer> {
	
	// Encuentra todos los stock en una tienda específica
    List<Stock> findByStore_CodeStore(String codeStore);
	
}