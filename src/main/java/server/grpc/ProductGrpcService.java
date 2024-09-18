package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.ProductGrpcServiceGrpc.ProductGrpcServiceImplBase;
import server.ProductProto.ProductGrpc;
import server.ProductProto.RequestId;
import server.services.IProductService;


@GrpcService
public class ProductGrpcService extends ProductGrpcServiceImplBase{

	@Autowired
	private IProductService services;

	
	@Override
	public void getProductByCode(RequestId request, StreamObserver<ProductGrpc> responseObserver) {
		 try {

			var response = services.findByCode(request.getCode());

			if (response == null) {
				
				String messageError = "User with code " + request.getCode() + " not found";
				
				throw new NoSuchElementException(messageError);
			}

			ProductGrpc product = 
					ProductGrpc
					.newBuilder()
					.setCode(response.getCode())
					.setName(response.getName())
					.setSize(response.getSize())
					.setPhoto(response.getPhoto())
					.setColor(response.getColor())
					.setActive(response.isActive())
					.build();

			responseObserver.onNext(product);
			responseObserver.onCompleted();

		}

		catch (NoSuchElementException e) {			
			
			responseObserver
			.onError(Status.NOT_FOUND.withDescription(e.getMessage())
			.asRuntimeException());
		}

		catch (Exception e) {
			
			String messageError = "Internal server error: " + e.getMessage();
			
			responseObserver
			.onError(Status.INTERNAL.withDescription(messageError)
			.asRuntimeException());
		}
		 
	}
}
