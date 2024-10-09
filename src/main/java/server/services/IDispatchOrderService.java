package server.services;

import java.util.List;

import server.entities.DispatchOrder;

public interface IDispatchOrderService {
	
	public DispatchOrder findByDispatchOrder(int dispatchOrder);

	public List<DispatchOrder> getAll();

	public boolean insertOrUpdate(DispatchOrder dispatchOrder, int idPurchaseOrder);

	public boolean remove(int dispatchOrder);

}
