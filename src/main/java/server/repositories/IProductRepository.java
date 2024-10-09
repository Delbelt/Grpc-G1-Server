package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import server.entities.Product;

public interface IProductRepository extends JpaRepository<Product, String> {
	
	public Product findByCode(String code);
	
	public Product findByName(String name);
	
	public List<Product> findAll();
	
	public List<Product> findAllByActive(boolean active);
	
	@Query("SELECT p FROM Product p WHERE "
	         + "(:code IS NULL OR p.code = :code) AND "
	         + "(:name IS NULL OR p.name = :name) AND "
	         + "(:size IS NULL OR p.size = :size) AND "
	         + "(:color IS NULL OR p.color = :color)")
	    List<Product> findProductsByFilter(
	        @Param("code") String code, 
	        @Param("name") String name, 
	        @Param("size") String size, 
	        @Param("color") String color
	    );
	}

	//public boolean deleteByCode(String code);
	//public List<Product> findProductsByFilter(String code, String name, String size, String color);

	//public boolean insertOrUpdate(Product product);
	 







