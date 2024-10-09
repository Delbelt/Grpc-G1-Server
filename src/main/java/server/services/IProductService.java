package server.services;

import java.util.List;

import server.entities.Product;

public interface IProductService {

	public Product findByCode(String code);
	
	public Product findByName(String name);

	public List<Product> findAll();
	
	public List<Product> findAllByActive(boolean active);

	public boolean insertOrUpdate(Product product);

	public boolean deleteByCode(String code);
	
	public List<Product> findProductsByFilter(String code, String name, String size, String color);
	
	public boolean updateProduct(Product product);
	
	public boolean modifyProduct(String code);

}