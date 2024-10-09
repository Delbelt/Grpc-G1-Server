package server.grpc;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.OrderProcessingGrpcServiceGrpc.OrderProcessingGrpcServiceImplBase;
import server.OrderProcessingProto.EmptyProcessing;
import server.OrderProcessingProto.ProcessingResponse;
import server.handlers.GrpcExceptionHandler;
import server.services.IOrderProcessingService;
import server.util.StatePurchaseOrder;

@GrpcService
public class OrderProcessingGrpcService extends OrderProcessingGrpcServiceImplBase {
	
	@Autowired
	private IOrderProcessingService service;
	
	public void runProcess(EmptyProcessing request, StreamObserver<ProcessingResponse> responseObserver) {
		
		try {
			
			service.processPurchaseOrders(StatePurchaseOrder.REQUESTED);
			
			ProcessingResponse reply = 
					ProcessingResponse
					.newBuilder()
					.setStatus("Orders processed correctly").
					build();					

			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		} 
		
		catch (Exception e) {

			GrpcExceptionHandler.handleException(e, responseObserver);
		}			
	}
}
