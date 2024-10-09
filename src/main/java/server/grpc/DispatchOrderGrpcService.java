package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.DispatchOrderGrpcServiceGrpc.DispatchOrderGrpcServiceImplBase;
import server.DispatchOrderProto.DispatchOrderGrpc;
import server.DispatchOrderProto.GetByDispatchOrderRequest;
import server.handlers.GrpcExceptionHandler;
import server.handlers.TimestampGrpcConverter;
import server.services.IDispatchOrderService;

@GrpcService
public class DispatchOrderGrpcService extends DispatchOrderGrpcServiceImplBase {

	@Autowired
	private IDispatchOrderService service;

	@Override
	public void getDispatchOrderGrpc(GetByDispatchOrderRequest request, StreamObserver<DispatchOrderGrpc> responseObserver) {

		try {

			var response = service.findByDispatchOrder(request.getDispatchOrder());

			if (response == null) {

				String messageError = "Dispatch order with code [" + request.getDispatchOrder() + "] not found";

				throw new NoSuchElementException(messageError);
			}

			DispatchOrderGrpc dispatchOrderGrpc = 
					DispatchOrderGrpc.newBuilder()
					.setDispatchOrder(response.getDispatchOrder())
					.setIdPurchaseOrder(response.getIdPurchaseOrder().getIdPurchaseOrder())
					.setEstimatedDate(TimestampGrpcConverter.toProtoDate(response.getEstimatedDate()))
					.build();

			responseObserver.onNext(dispatchOrderGrpc);
			responseObserver.onCompleted();
		}

		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}

	}
}
