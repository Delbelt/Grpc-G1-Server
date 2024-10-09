package server.services;

import java.time.LocalDate;
import java.util.List;

import server.entities.PurchaseOrder;

public interface IPurchaseOrderService {
	
	public PurchaseOrder findById(int id);

	public List<PurchaseOrder> getAll();
	
	public List<PurchaseOrder> getAllByDate(LocalDate date);
	
	public List<PurchaseOrder> getAllByCodeItem(LocalDate date);
	
	public List<PurchaseOrder> getAllFromState(String state);
	
	public PurchaseOrder getByIdRelationship(int id);
	
	public PurchaseOrder insert(PurchaseOrder order);

	public boolean insertOrUpdate(PurchaseOrder order, String state);
	
	public boolean Update(PurchaseOrder order);

	public boolean remove(int id);
}
