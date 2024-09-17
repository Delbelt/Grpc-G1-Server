package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
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
	public Stock findById(int IdStore) {
		
		var response = repository.findById(IdStore).orElse(null);
		
		log.info("[StockService][findById]: " + response);
		
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
		return false;
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
