package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.OrderItemGrpcServiceGrpc.OrderItemGrpcServiceImplBase;
import server.orderItemProto.GetByIdRequest;
import server.orderItemProto.OrderItemGrpc;
import server.handlers.GrpcExceptionHandler;
import server.services.IOrderItemService;

@GrpcService
public class OrderItemGrpcService extends OrderItemGrpcServiceImplBase {

	@Autowired
	private IOrderItemService service;

	@Override
	public void getPurcharseOrderGrpc(GetByIdRequest request, StreamObserver<OrderItemGrpc> responseObserver) {

		try {

			var response = service.findById(request.getId());		

			if (response == null) {

				String messageError = "Order item with id [" + request.getId() + "] not found";

				throw new NoSuchElementException(messageError);
			}
			
			OrderItemGrpc orderItem =		
					OrderItemGrpc
					.newBuilder()
					.setCode(response.getCode())
					.setColor(response.getColor())
					.setSize(response.getSize())
					.setQuantity(response.getQuantity())
					.build();		
			
			responseObserver.onNext(orderItem);
			responseObserver.onCompleted();
		}

		catch (Exception e) {
			
			GrpcExceptionHandler.handleException(e, responseObserver);
		}
	}
}
