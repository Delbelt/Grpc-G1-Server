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
	@Transactional(readOnly = true)
	public List<Product> findAllByActive(boolean active) {
		
		return repository.findAllByActive(active);
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
	    
	    @Override
	    @Transactional
	    public boolean updateProduct(Product product) {
	        try {
	            // Verificar si el producto existe antes de intentar actualizarlo
	            if (repository.existsById(product.getCode())) {
	                repository.save(product); // Guarda el producto modificado
	                return true; 
	            } else {
	                return false; // Si no existe, retorna false
	            }
	        } catch (Exception e) {
	        
	            log.error("Error updating product: " + e.getMessage());
	            return false;
	        }
	    }

		@Override
		public boolean modifyProduct(String code) {
			 try {
		            // Verificar si el producto existe antes de intentar actualizarlo
		            if (repository.findByCode(code) != null) {
		            	var product = repository.findByCode(code);
		                repository.save(product); // Guarda el producto modificado
		                return true; 
		            } else {
		                return false; // Si no existe, retorna false
		            }
		        } catch (Exception e) {
		        
		            log.error("Error updating product: " + e.getMessage());
		            return false;
		        }
		}

}
