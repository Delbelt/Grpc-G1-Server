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
	
	@Override
    public void getAllProducts(Empty request, StreamObserver<ProductList> responseObserver) {
        try {
            var products = services.findAll();

            // Mapea la lista de entidades Product a la lista de mensajes ProductGrpc
            ProductList.Builder productListBuilder = ProductList.newBuilder();
            for (Product product : products) {
                ProductGrpc productGrpc = ProductGrpc.newBuilder()
                    .setCode(product.getCode())
                    .setName(product.getName())
                    .setSize(product.getSize())
                    .setPhoto(product.getPhoto())
                    .setColor(product.getColor())
                    .setActive(product.isActive())
                    .build();
                productListBuilder.addProducts(productGrpc);
            }

            // Envía la lista de productos al cliente
            responseObserver.onNext(productListBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
                .asRuntimeException()
            );
        }
    }
}
