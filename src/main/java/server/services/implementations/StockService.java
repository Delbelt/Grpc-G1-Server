package server.services.implementations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.Product;
import server.entities.Stock;
import server.repositories.IProductRepository;
import server.repositories.IStockRepository;
import server.repositories.IStoreRepository;
import server.services.IStockService;

@Slf4j
@Service
public class StockService implements IStockService{
	
	
	@Autowired
	private IStockRepository repository;
	@Autowired
    private IStoreRepository storeRepository; // Repositorio para obtener la tienda

    @Autowired
    private IProductRepository productRepository; // Repositorio para obtener el producto
	
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

	@Override
    @Transactional(readOnly = true)
    public List<Stock> findAvailableStocks() {
        List<Stock> availableStocks = repository.findByQuantityGreaterThan(0);
        log.info("[StockService][findAvailableStocks]: Available stocks found: {}", availableStocks);
        return availableStocks;
    }
	
	@Override
    @Transactional(readOnly = true)
    public List<Stock> findUnavailableStocks() {
        List<Stock> unavailableStocks = repository.findByQuantity(0);
        log.info("[StockService][findUnavailableStocks]: Unavailable stocks found: {}", unavailableStocks);
        return unavailableStocks;
    }
	
	@Override
	@Transactional(readOnly = true)
	public List<Stock> getStockByProduct(String productCode) {
	    List<Stock> stocks = repository.findByProduct_Code(productCode);
	    log.info("[StockService][getStockByProduct]: Stocks found for product {}: {}", productCode, stocks);
	    return stocks;
	}

	@Override
    @Transactional
    public Stock createStock(String storeCode, String productCode) {
        try {
            // Obtener la tienda por código
            var store = storeRepository.findByCode(storeCode);
            if (store == null) {
                throw new NoSuchElementException("Store with code " + storeCode + " not found");
            }

            // Obtener el producto por código
            var product = productRepository.findByCode(productCode);
            if (product == null) {
                throw new NoSuchElementException("Product with code " + productCode + " not found");
            }

            // Crear el nuevo stock
            Stock stock = new Stock();
            stock.setCode(UUID.randomUUID().toString()); // Generar un código único
            stock.setStore(store);
            stock.setProduct(product);
            stock.setQuantity(0); // Cantidad inicial 0

            // Guardar el stock en la base de datos
            repository.save(stock);

            log.info("[StockService][createStock]: Stock created for store {} and product {} with quantity 0", storeCode, productCode);

            return stock;
        } catch (Exception e) {
            log.error("[StockService][createStock]: Error creating stock", e);
            throw new RuntimeException("Error creating stock: " + e.getMessage());
        }
    }
	@Override
	@Transactional(readOnly = true)
	public boolean stockExists(String productCode, String storeCode) {
	    return repository.findByProduct_CodeAndStore_Code(productCode, storeCode).isPresent();
	}
}
