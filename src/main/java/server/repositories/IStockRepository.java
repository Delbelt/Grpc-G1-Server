package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Product;
import server.entities.Stock;

public interface IStockRepository extends JpaRepository<Stock, String> {
	
	// Encuentra por id de stock
	public Stock findByCode(String code);
	// Encuentra todos los stock en una tienda especifica
    List<Stock> findByStore_Code(String codeStore);
    // Encuentra todos los stock disponibles (Cantidad mayor a 0)
    List<Stock> findByQuantityGreaterThan(int quantity);
	
}