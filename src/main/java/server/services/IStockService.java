package server.services;

import java.util.List;

import server.entities.Stock;

public interface IStockService {
	
	public Stock findByCode(String code);
	
	public List<Stock> getStockByStore(String codeStore); // Obtiene lista de stocks por codigo de tienda (codeStore)

	public List<Stock> getAll();

	public boolean insertOrUpdate(Stock stock); // Agrega o modifica un stock

	public boolean remove(String code);
	
	public List<Stock> findAvailableStocks(); // Obtiene lista de todos stocks disponibles

	public List<Stock> findUnavailableStocks(); // Obtiene lista de todos stocks no disponibles

	public List<Stock> getStockByProduct(String productCode); // Obtiene lista de todos los stock que contienen el producto

	public Stock createStock(String storeCode, String productCode, int quantity);
	
	public boolean stockExists(String productCode, String storeCode);
	
	public Stock findByStoreAndProduct(String codeStore, String codeProduct);

	public Stock addStock(String code, int quantityToAdd);

	public Stock subtractStock(String code, int quantityToSubtract);
}