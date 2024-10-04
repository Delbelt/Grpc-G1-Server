package server.services.implementations;

import java.util.ArrayList;

import java.util.List;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.Product;
import server.entities.Stock;
import server.entities.Store;
import server.entities.User;
import server.repositories.IProductRepository;
import server.repositories.IStockRepository;
import server.repositories.IStoreRepository;
import server.services.IStoreService;

@Slf4j
@Service
public class StoreService implements IStoreService{
	
	

	@Autowired
    private IStoreRepository repository;
	
	@Autowired
	private IProductRepository productRepository;
	
	@Autowired
	private IStockRepository stockRepository;

	@Autowired
	private StockService stockService;
	
	private UserService userService;
	
	@Override
	@Transactional(readOnly = true)
	public Store getStoreByCode(String code) {
	    Store store = repository.findByCode(code);
	    
	    if (store == null) {
	        throw new NoSuchElementException("Store with code " + code + " not found");
	    }
	    log.info("Store: " + store.getCode() + ", Stocks: " + store.getStocks().size());
	    return store; 
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Store> getStoresByState(boolean active) {
	    List<Store> stores = repository.findByActive(active);

	    if (stores.isEmpty()) {
	        throw new NoSuchElementException("No stores found for the requested state.");
	    }

	    return stores;
	}
    @Override
    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Store createStore(Store store) {
        
        var response = repository.save(store);
        log.info("[StoreService][createStore]: Creating store with code {}", store.getCode());
        return response;
    }
    
    @Override
    @Transactional
    public Store changeStoreState(String code, boolean active) {
        // Buscar la tienda por código
        Store store = repository.findByCode(code);
                

        // Actualizar el estado
        store.setActive(active);
        log.info("[StoreService][changeStoreState]: Store with code {} is now {}", code, active ? "enabled" : "disabled");

        
        return repository.save(store);
    }

    
    @Override
    @Transactional
    public Store updateStore(Store store) {
        var existingStore = repository.findByCode(store.getCode());
        if (existingStore == null) {
            log.warn("[StoreService][updateStore]: Store with code {} not found", store.getCode());
            throw new NoSuchElementException("Store with code " + store.getCode() + " not found");
        }
        
        var response = repository.save(store);
        log.info("[StoreService][updateStore]: Updated store: " + response);
        return response;
    }
   
    @Override
    @Transactional
    public boolean deleteStore(String code) {
        boolean isDeleted = false;
        try {
            if (repository.existsById(code)) { // Verifica si la tienda existe
                repository.deleteById(code);
                isDeleted = true;
                log.info("[StoreService][deleteStore]: Successfully deleted store with ID " + code);
            } else {
                log.warn("[StoreService][deleteStore]: Store with ID {} not found", code);
            }
        } catch (Exception e) {
            log.error("[StoreService][deleteStore]: Error occurred while trying to delete store with ID " + code, e);
        }
        return isDeleted;
    }
    
    @Override
    @Transactional
    public void assignProductToStore(String storeCode, String productCode) {
        // 1. Verificar que la tienda existe
        Store store = repository.findByCode(storeCode);
        if (store == null) {
            throw new NoSuchElementException("Store not found with code: " + storeCode);
        }   

        
        Product product = productRepository.findByCode(productCode);
        if (product == null) {
            throw new NoSuchElementException("Product not found with code: " + productCode);
        }

        
        if (stockRepository.findByProduct_CodeAndStore_Code(productCode, storeCode).isPresent()) {
            String messageError = "Stock already exists for product " + productCode + " and store " + storeCode;
            throw new NoSuchElementException(messageError);
        }

        
        Stock newStock = stockService.createStock(storeCode, productCode, 0); 

        log.info("Successfully assigned product {} to store {} with stock code {}", productCode, storeCode, newStock.getCode());
    }
    
    
    @Override
    @Transactional
    public void assignUserToStore(String storeCode, int userId) {
        
        Store store = repository.findByCode(storeCode);
        if (store == null) {
            throw new NoSuchElementException("Store not found with code: " + storeCode);
        }
       
        User user = userService.findById(userId); 
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }

        
        store.getUsers().add(user); 
        repository.save(store); 
        log.info("Successfully assigned user {} to store {}", userId, storeCode);
    }
    
   

    
    @Override
    @Transactional
    public void removeProductFromStore(String storeCode, String productCode) {
        Store store = repository.findByCode(storeCode);
        if (store == null) {
            throw new NoSuchElementException("Store with code " + storeCode + " not found");
        }

        Stock stock = stockRepository.findByStoreAndProduct(storeCode, productCode);
        if (stock == null) {
            throw new NoSuchElementException("Stock with product " + productCode + " not found in store " + storeCode);
        }

        store.getStocks().remove(stock);
        repository.save(store);

        log.info("Successfully removed product {} from store {}", productCode, storeCode);
    }

    @Override
    @Transactional
    public void removeUserFromStore(String storeCode, int userId) {
        
        Store store = repository.findByCode(storeCode);
        if (store == null) {
            throw new NoSuchElementException("Store not found with code: " + storeCode);
        }

       
        User user = userService.findById(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }

        
        store.getUsers().remove(user); 
        repository.save(store); 
        log.info("Successfully removed user {} from store {}", userId, storeCode);
    }

}
