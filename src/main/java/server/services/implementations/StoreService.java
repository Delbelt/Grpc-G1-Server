package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.Store;
import server.repositories.IStoreRepository;
import server.services.IStoreService;

@Slf4j
@Service
public class StoreService implements IStoreService{
	
	

	@Autowired
    private IStoreRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Store getStoreByCode(String code) {
        var response = repository.findByCode(code);
        log.info("[StoreService][getStoreByCode]: " + response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Store> getAllStores() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Store createStore(Store store) {
        // You might want to add validation or additional logic here
        var response = repository.save(store);
        log.info("[StoreService][createStore]: Created or updated store: " + response);
        return response;
    }

    @Override
    @Transactional
    public Store updateStore(Store store) {
        // Ensure the store exists before updating, or handle accordingly
        var existingStore = repository.findByCode(store.getCode());
        if (existingStore != null) {
            var response = repository.save(store);
            log.info("[StoreService][updateStore]: Updated store: " + response);
            return response;
        } else {
            // Handle store not found case if needed
            log.warn("[StoreService][updateStore]: Store with code {} not found", store.getCode());
            return null;
        }
    }

   
    @Override
	@Transactional
    public boolean deleteStore(String code) {
    	boolean isDelete = false;
        try {
            repository.deleteById(code);
     
            isDelete = true;
        } catch (Exception e) {
            
        }
        return isDelete;
    }
}
