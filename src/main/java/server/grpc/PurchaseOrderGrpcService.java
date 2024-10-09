package server.grpc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import server.PurcharseOrderProto.EmptyAll;
import server.PurcharseOrderProto.GetByDispatchOrderRequest;
import server.PurcharseOrderProto.PostPurchaseOrderRequest;
import server.PurcharseOrderProto.PurchaseOrderGrpc;
import server.PurcharseOrderProto.PurchaseOrders;
import server.PurcharseOrderProto.RequestAllByState;
import server.PurchaseOrderGrpcServiceGrpc.PurchaseOrderGrpcServiceImplBase;
import server.orderItemProto.OrderItemGrpc;
import server.entities.OrderItem;
import server.entities.PurchaseOrder;
import server.handlers.GrpcExceptionHandler;
import server.handlers.TimestampGrpcConverter;
import server.services.IPurchaseOrderService;
import server.services.IStoreService;
import server.util.StatePurchaseOrder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PurchaseOrderGrpcService extends PurchaseOrderGrpcServiceImplBase {

	@Autowired
	private IPurchaseOrderService service;

	@Autowired
	private IStoreService storeService;

//	private final KafkaMessageProducer kafkaMessageProducer;
//
//    @Autowired
//    public PurchaseOrderGrpcService(KafkaMessageProducer kafkaMessageProducer) {
//        this.kafkaMessageProducer = kafkaMessageProducer;
//    }

	// TODO: agregar en .proto el id de la tienda

	private List<OrderItemGrpc> convertOrderItemToGrpc(List<OrderItem> list) {

		List<OrderItemGrpc> itemGrpcList = new ArrayList<>();

		for (OrderItem orderItem : list) {

			OrderItemGrpc orderItemGrpc = OrderItemGrpc.newBuilder().setCode(orderItem.getCode())
					.setColor(orderItem.getColor()).setSize(orderItem.getSize()).setQuantity(orderItem.getQuantity())
					.build();

			itemGrpcList.add(orderItemGrpc);
		}

		return itemGrpcList;
	}

	@Override // ORDEN DE COMPRA NO RESUELTA - TODO: agregar metodo:
				// getPurchaseOrderDetailGrpc para las resueltas (con todos los datos)
	public void getPurchaseOrderGrpc(GetByDispatchOrderRequest request,
			StreamObserver<PurchaseOrderGrpc> responseObserver) {

		try {

			var response = service.getByIdRelationship(request.getIdPurchaseOrder());

			if (response == null) {

				String messageError = "Purcharse order with code [" + request.getIdPurchaseOrder() + "] not found";

				throw new NoSuchElementException(messageError);
			}

			List<OrderItemGrpc> itemsGrpcList = convertOrderItemToGrpc(response.getItems());

			PurchaseOrderGrpc purcharseOrden = PurchaseOrderGrpc.newBuilder()
					.setIdPurchaseOrder(response.getIdPurchaseOrder()).setObservations(response.getObservations())
					.setState(response.getState())
					.setRequestDate(TimestampGrpcConverter.toProtoTimestamp(response.getRequestDate()))
					.addAllItems(itemsGrpcList).build();

			responseObserver.onNext(purcharseOrden);
			responseObserver.onCompleted();
		}

		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}
	}

	public void postPurchaseOrderGrpc(PostPurchaseOrderRequest request,
			StreamObserver<PurchaseOrderGrpc> responseObserver) {

		try {

			var store = storeService.getStoreByCode(request.getCodeStore());

			LocalDateTime requestDate = LocalDateTime.now();
			String initialState = StatePurchaseOrder.REQUESTED;
			String initialObservations = "Sin observaciones";

			List<OrderItem> items = new ArrayList<>();

			for (OrderItemGrpc requestItem : request.getItemsList()) {

				OrderItem item = new OrderItem(requestItem.getCode(), requestItem.getColor(), requestItem.getSize(),
						requestItem.getQuantity());

				items.add(item);
			}

			PurchaseOrder order = new PurchaseOrder(store, initialState, requestDate, initialObservations, items);

			var response = service.insert(order);

			if (response == null) {

				String messageError = "The operation failed";

				throw new IllegalStateException(messageError);
			}

			PurchaseOrderGrpc purcharseOrden = PurchaseOrderGrpc.newBuilder()
					.setIdPurchaseOrder(response.getIdPurchaseOrder()).setObservations("Sin observaciones")
					.setState(response.getState())
					.setRequestDate(TimestampGrpcConverter.toProtoTimestamp(response.getRequestDate())).build();

			// kafkaMessageProducer.sendObjectMessage(TopicsKafka.PURCHASE_ORDER, order);

			responseObserver.onNext(purcharseOrden);
			responseObserver.onCompleted();
		}

		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}
	}

	public void getAllPurchaseOrderGrpc(EmptyAll request, StreamObserver<PurchaseOrders> responseObserver) {

		try {

			var orders = service.getAll();

			PurchaseOrders.Builder PurchaseOrdersBuilder = PurchaseOrders.newBuilder();

			for (PurchaseOrder order : orders) {

				List<OrderItemGrpc> itemsGrpcList = convertOrderItemToGrpc(order.getItems());

				PurchaseOrderGrpc purchaseOrderGrpc = PurchaseOrderGrpc.newBuilder()
						.setIdPurchaseOrder(order.getIdPurchaseOrder()).setObservations(order.getObservations())
						.setState(order.getState())
						.setRequestDate(TimestampGrpcConverter.toProtoTimestamp(order.getRequestDate()))
						.addAllItems(itemsGrpcList).build();

				PurchaseOrdersBuilder.addOrders(purchaseOrderGrpc);
			}

			responseObserver.onNext(PurchaseOrdersBuilder.build());
			responseObserver.onCompleted();
		}

		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}
	}

	public void getAllByStatePurchaseOrderGrpc(RequestAllByState request,
			StreamObserver<PurchaseOrders> responseObserver) {

		try {

			var orders = service.getAllFromState(request.getState());

			PurchaseOrders.Builder PurchaseOrdersBuilder = PurchaseOrders.newBuilder();

			for (PurchaseOrder order : orders) {

				List<OrderItemGrpc> itemsGrpcList = convertOrderItemToGrpc(order.getItems());

				PurchaseOrderGrpc purchaseOrderGrpc = PurchaseOrderGrpc.newBuilder()
						.setIdPurchaseOrder(order.getIdPurchaseOrder()).setObservations(order.getObservations())
						.setState(order.getState())
						.setRequestDate(TimestampGrpcConverter.toProtoTimestamp(order.getRequestDate()))
						.addAllItems(itemsGrpcList).build();

				PurchaseOrdersBuilder.addOrders(purchaseOrderGrpc);
			}

			responseObserver.onNext(PurchaseOrdersBuilder.build());
			responseObserver.onCompleted();
		}

		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}
	}
}
