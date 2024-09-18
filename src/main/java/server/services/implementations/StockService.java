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
	
	// Obtiene lista de stocks por codigo de tienda (codeStore)
	@Override
	@Transactional(readOnly = true)
    public List<Stock> getStockByStore(String codeStore) {
		
        var response = repository.findByStore_CodeStore(codeStore);
        
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
	public boolean remove(int idStore) {

	    boolean isDeleted = false;

	    try {
	        repository.deleteById(idStore);
	        isDeleted = true;
	        log.info("[StockService][remove]: Successfully deleted stock with ID " + idStore);
	    } catch (Exception e) {
	        log.error("[StockService][remove]: Error occurred while trying to delete stock with ID " + idStore, e);
	    }

	    return isDeleted;
	}

}
