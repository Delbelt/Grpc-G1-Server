package server.services;

import java.util.List;

import server.entities.Product;
import server.entities.Stock;

public interface IStockService {
	
	public Stock findByCode(String code);
	
	public List<Stock> getStockByStore(String codeStore); // Obtiene lista de stocks por codigo de tienda (codeStore)

	public List<Stock> getAll();

	public boolean insertOrUpdate(Stock stock); // Agrega o modifica un stock

	public boolean remove(String code);
	
	public List<Stock> findAvailableStocks(); // Obtiene lista de todos stocks disponibles


	
}