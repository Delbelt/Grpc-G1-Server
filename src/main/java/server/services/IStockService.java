package server.services;

import java.util.List;

import server.entities.Product;
import server.entities.Stock;

public interface IStockService {
	
	public Stock findByCode(String code);
	
	List<Stock> getStockByStore(String codeStore); // Obtiene lista de stocks por codigo de tienda (codeStore)

	public List<Stock> getAll();

	public boolean insertOrUpdate(Stock stock); // Agrega o modifica un stock

	public boolean remove(String code);


	
}