package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.Product;
import server.repositories.IProductRepository;
import server.services.IProductService;


@Slf4j
@Service
public class ProductService implements IProductService{

	@Autowired
	private  IProductRepository repository;
	
	@Override
	@Transactional(readOnly = true)
	public Product findByCode (String code) 
	{
		var response = repository.findByCode(code);
		log.info("[ProductService][findByCode]: " + response);

		return response;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Product findByName(String name) {
		
		return repository.findByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Product> findAll() {

		return repository.findAll(); // retorna la lista de productos
	}
	
	@Override
	@Transactional
	public boolean insertOrUpdate(Product product) {

		// TODO enhance handler response and exception

		return repository.save(product) != null ? true : false;
	}
	
	@Override
	@Transactional
	public boolean deleteByCode(String code) {

		boolean isDeleted = false;

		try {
			repository.deleteById(code);
			isDeleted = true;
		}

		catch (Exception e) {
			// TODO: handle exception
		}

		return isDeleted;
	}

	
	
	    @Override
	    @Transactional(readOnly = true)
	    public List<Product> findProductsByFilter(String code, String name, String size, String color) {
	        List<Product> filteredProducts = repository.findProductsByFilter(
	            code.isEmpty() ? null : code,
	            name.isEmpty() ? null : name,
	            size.isEmpty() ? null : size,
	            color.isEmpty() ? null : color
	        );
	        log.info("[ProductService][findProductsByFilter]: " + filteredProducts);
	        return filteredProducts;
	    }
}
