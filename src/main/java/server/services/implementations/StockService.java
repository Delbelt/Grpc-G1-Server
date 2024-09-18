package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.Product;
import server.entities.Stock;
import server.repositories.IStockRepository;
import server.services.IStockService;

@Slf4j
@Service
public class StockService implements IStockService{
	
	
	@Autowired
	private IStockRepository repository;
	
	@Override
	@Transactional(readOnly = true)
    public Stock findByCode(String code) {
		var response = repository.findByCode(code);
		
		log.info("[StockService][findByCode]: " + response);

		return response;
    }
	
	// Obtiene lista de stocks por codigo de tienda (codeStore)
	@Override
	@Transactional(readOnly = true)
    public List<Stock> getStockByStore(String codeStore) {
		
        var response = repository.findByStore_Code(codeStore);
        
        log.info("[StockService][getStockByStore]: Products found for store {}: {}", codeStore, response);
        
        return response;
    }

	@Override
	@Transactional(readOnly = true)
	public List<Stock> getAll() {
		
		return repository.findAll();
	}

	@Override
	public boolean insertOrUpdate(Stock stock) {
		// TODO Auto-generated method stub
		return repository.save(stock) != null ? true : false;
	}

	@Override
	@Transactional
	public boolean remove(String code) {

	    boolean isDeleted = false;

	    try {
	    	repository.deleteById(code);
	        isDeleted = true;
	        log.info("[StockService][remove]: Successfully deleted stock with ID " + code);
	    } catch (Exception e) {
	        log.error("[StockService][remove]: Error occurred while trying to delete stock with ID " + code, e);
	    }

	    return isDeleted;
	}

}
