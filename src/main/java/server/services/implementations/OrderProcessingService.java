package server.services.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import server.entities.DispatchOrder;
import server.entities.OrderItem;
import server.entities.PurchaseOrder;
import server.entities.Stock;
import server.kafka.KafkaMessageProducer;
import server.services.IDispatchOrderService;
import server.services.IOrderProcessingService;
import server.services.IPurchaseOrderService;
import server.services.IStockService;
import server.util.StatePurchaseOrder;
import server.util.TopicsKafka;

@Service
public class OrderProcessingService implements IOrderProcessingService {

	@Autowired
	private IPurchaseOrderService purchaseOrderService;

	@Autowired
	private IStockService stockService;

	@Autowired
	private IDispatchOrderService dispatchService;
	
	private final KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    public OrderProcessingService(KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

	@Transactional
	public void processPurchaseOrders(String state) {
		
		var purchaseOrderResponse = purchaseOrderService.getAllFromState(state);
		
		boolean hasPurchaseOrders = purchaseOrderResponse.size() > 0;

		if (hasPurchaseOrders) {

			for (PurchaseOrder order : purchaseOrderResponse) {

				if (order.getState().equalsIgnoreCase(state)) {

					var items = order.getItems();

					List<OrderItem> missingItems = new ArrayList<>();
					
					List<Stock> stocksProviderToModify = new ArrayList<>();
					List<Stock> stocksClientToModify = new ArrayList<>();

					for (OrderItem item : items) {
						
						String Provider = StatePurchaseOrder.PROVIDER;
						
						var checkStockProvider = stockService.findByStoreAndProduct(Provider, item.getCode());
									
						// TIENDA - PRODUCTO					
						var checkStockClient = stockService.findByStoreAndProduct(order.getStore().getCode(), item.getCode());				
						
						boolean isRejected = item.getQuantity() < 1 || checkStockProvider == null;

						if (isRejected) {
							
							String errorNotFound = "Articulo[" + item.getCode() + "]: No existe";
							String errorQuantity = "Articulo[" + item.getCode() + "]: Cantidad mal informada";
							
							String rejectedMessage = checkStockProvider == null ? errorNotFound : errorQuantity;
							
							TopicRejected(order, rejectedMessage);	
						}
						
						boolean exceedsStock = item.getQuantity() > checkStockProvider.getQuantity();
					
						if (exceedsStock) {
							
							missingItems.add(item);
						} 
						
						else {							
						
							checkStockProvider.setQuantity(checkStockProvider.getQuantity() - item.getQuantity());
							checkStockClient.setQuantity(checkStockClient.getQuantity() + item.getQuantity());

							stocksProviderToModify.add(checkStockProvider);
							stocksClientToModify.add(checkStockClient);
						}
					}
					
					boolean hasMissingItem = missingItems.size() > 0 && state.equalsIgnoreCase(StatePurchaseOrder.REQUESTED);

					if (hasMissingItem) {
						
						TopicPaused(order);						
					}
					
					else {
						
						boolean isValidRequest = state.equalsIgnoreCase(StatePurchaseOrder.REQUESTED);
											
						if (isValidRequest) {	
							
							TopicRequested(order, stocksProviderToModify, stocksClientToModify);							
						}
					}
				}
			}
		} 
		
		else {
			
			// No deberia hacer nada
			System.out.println("NO HAY NUEVAS ORDENES DE COMPRA");
		}
	}
	
	public void TopicRejected(PurchaseOrder order, String message) {
		
		try {
			
			order.setObservations("Order rechazada por faltantes o error al cargar la orden");
			purchaseOrderService.insertOrUpdate(order, StatePurchaseOrder.REJECTED);
			String topic = order.getStore().getCode() + TopicsKafka.REQUESTED;			
			kafkaMessageProducer.sendMessage(topic, message);
		}

		catch (InterruptedException e) {
	
			e.printStackTrace();								
		}
		
		catch (ExecutionException e) {
			
			e.printStackTrace();
		}
	}

	@Transactional
	public void TopicPaused(PurchaseOrder order) {
		
		
		String topic = order.getStore().getCode() + TopicsKafka.REQUESTED;
		String statusMessage = "PAUSADA";
		
		try {
			order.setObservations("Order pausada por falta de stock");
			purchaseOrderService.insertOrUpdate(order, StatePurchaseOrder.PAUSED);
			kafkaMessageProducer.sendMessage(topic, statusMessage);
		} 
		
		catch (InterruptedException e) {			
			e.printStackTrace();
		} 
		
		catch (ExecutionException e) {
			
			e.printStackTrace();
		}	
	}
	
	@Transactional
	public void TopicRequested(PurchaseOrder order, List<Stock> stocksProviderToModify, List<Stock> stocksClientToModify) {
		try {
			
			DispatchOrder dispatchOrder = new DispatchOrder();
			
			dispatchService.insertOrUpdate(dispatchOrder, order.getIdPurchaseOrder());

			// Se modifica el stock del proveedor
			for (Stock stock : stocksProviderToModify) {
				
				stockService.insertOrUpdate(stock);
			}

			// Se modifica el stock de la tienda
			for (Stock stock : stocksClientToModify) {
				
				stockService.insertOrUpdate(stock);
			}
			
			LocalDateTime receiptDate = LocalDateTime.now();
			
			order.setReceiptDate(receiptDate);
			order.setObservations("Orden aceptada");
			purchaseOrderService.Update(order);
			
			String topic = order.getStore().getCode() + TopicsKafka.REQUESTED;								
			
			String statusMessage = StatePurchaseOrder.ACCEPTED;
			kafkaMessageProducer.sendMessage(topic, statusMessage);
			kafkaMessageProducer.sendObjectMessage(TopicsKafka.RECEPTION, dispatchOrder);			
			kafkaMessageProducer.sendObjectMessage(TopicsKafka.DISPATCH, dispatchOrder);							
		}

		catch (InterruptedException e) {
	
			e.printStackTrace();								
		}
		
		catch (ExecutionException e) {
			
			e.printStackTrace();
		}
	}
}
