package server.services;

import java.util.List;

import server.entities.Product;
import server.entities.User;

public interface IProductService {

	public Product findByCode(String code);
	
	public Product findByName(String name);

	public List<Product> findAll();

	public boolean insertOrUpdate(Product product);

	public boolean deleteByCode(String code);
	
	public List<Product> findProductsByFilter(String code, String name, String size, String color);

}