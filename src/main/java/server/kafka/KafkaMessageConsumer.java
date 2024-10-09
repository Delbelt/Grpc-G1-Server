package server.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import server.entities.PurchaseOrder;
import server.util.TopicsKafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {
	
	// TODO: utilizarlo mas adelante para futuras entregas.

	private final ObjectMapper objectMapper = new ObjectMapper();

//	@KafkaListener(topics = TopicsKafka.PURCHASE_ORDER, groupId = TopicsKafka.groupIdTopics)
	public void consumePurchaseOrder(String message) {
		
		// TODO: implementar el proceso por orden de compra con getByIdRelationship
		// y enviar al topic de /{codigo de tienda}/solicitudes
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
	/*
	
	@KafkaListener(topics = TopicsKafka.REQUESTS, groupId = TopicsKafka.groupIdTopics)
	public void consumeRequests(String message) {
		
		// TODO: metodo dinamico de /{codigo de tienda}/solicitudes
		// para actualizar el estado de la orden de compra segun el id de la orden de compra.
		// La idea seria que sea dinamico y que cambie siempre que se haga el proceso automatico de kafka de 
		// consumePurchaseOrder
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
	@KafkaListener(topics = TopicsKafka.PURCHASE_ORDER, groupId = TopicsKafka.groupIdTopics)
	public void consumePurchaseOrderPaused(String message) {
		
		// TODO: implementar el proceso de actualizacion segun el producto
		// Aca se deberia implementar que al momento de actualizar un producto (validar por codigo)
		// Re-lanzar las ordenes por codigo
		// purchaseOrderService.getAllByCodeProduct
		// Deberia implementarse un nuevo topic como /updateStock para poder ser escuchado y actualizar las ordenes si la tienen.
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
	@KafkaListener(topics = TopicsKafka.PURCHASE_ORDER, groupId = TopicsKafka.groupIdTopics)
	public void consumePurchaseOrderNews(String message) {
		
		// TODO: Cuando se den de alta los productos en la web del proveedor
		// se deberia enviar aca y registrar como productos (con activo = false) 
		// para referenciar que no fueron dados de alta
		// de esta forma se resuelven los 2 casos (los nuevos productos y que se muestren las novedades en las tiendas)
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
		
	@KafkaListener(topics = TopicsKafka.DISPATCH, groupId = TopicsKafka.groupIdTopics)
	public void consumeDispatch(String message) {
		
		// TODO: metodo dinamico de "/{codigo de tienda}/despacho" para que cuando se acepte
		// el proveedor reste de su stock la solicitud aprobada y generar la orden de despacho
		// En este paso ademas... se deberia actualizar de forma simbolica
		// que llego la mercaderia..y actualizar el dato fecha que recibio
		// notificar al proveedor con /recepcion
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}
	
	@KafkaListener(topics = TopicsKafka.DISPATCH, groupId = TopicsKafka.groupIdTopics)
	public void consumeReceipt(String message) {
		
		// TODO: cuando el usuario recibe la mercaderia, se actualiza la fecha que recibio el pedido
		
		try {			
				
			PurchaseOrder order = objectMapper.readValue(message, PurchaseOrder.class);
			
			System.out.println(order.getIdPurchaseOrder());
			
			// purchaseOrderService.getByIdRelationship(order.getIdPurchaseOrder())
			
			System.out.println("Recibido: " + order);
		} 
		
		catch (Exception e) {
			
			System.err.println("Error al deserializar el mensaje: " + e.getMessage());
		}
	}

	 */
}
