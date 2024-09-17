package server.services;

import java.util.List;

import server.entities.Stock;

public interface IStockService {
	
	public Stock findById(int IdStore);

	public List<Stock> getAll();

	public boolean insertOrUpdate(Stock stock); // agrega o modifica un stock

	public boolean remove(int IdStore);
}