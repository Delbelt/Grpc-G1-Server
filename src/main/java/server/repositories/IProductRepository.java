package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Product;

public interface IProductRepository extends JpaRepository<Product, String> {
	public Product findByCode(String code);
	public Product findByName(String name);
	public List<Product> findAll(); // este metodo rompio todo arreglar el mvn
	//public boolean insertOrUpdate(Product product);
	//public boolean deleteByCode(String code); 
}






