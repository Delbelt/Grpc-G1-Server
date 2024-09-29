package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.ProductGrpcServiceGrpc.ProductGrpcServiceImplBase;
import server.ProductProto.Empty;
import server.ProductProto.ProductGrpc;
import server.ProductProto.ProductList;
import server.ProductProto.RequestId;
import server.ProductProto.ResponseMessage;
import server.entities.Product;
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
			        .setPhoto(ByteString.copyFrom(response.getPhoto()))  // Usa 'response' en lugar de 'product'
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
                    .setPhoto(ByteString.copyFrom(product.getPhoto()))
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
	
	@Override
	public void createProduct(ProductGrpc request, StreamObserver<ResponseMessage> responseObserver) {
	    try {
	        // Crear una nueva entidad Product a partir de los datos recibidos
	        Product newProduct = new Product();
	        newProduct.setCode(request.getCode());
	        newProduct.setName(request.getName());
	        newProduct.setSize(request.getSize());
	        newProduct.setPhoto(request.getPhoto().toByteArray()); // Convertimos ByteString a byte[]
	        newProduct.setColor(request.getColor());
	        newProduct.setActive(request.getActive());

	        // Usar el servicio para insertar o actualizar el producto
	        boolean isSaved = services.insertOrUpdate(newProduct);

	        // Preparar la respuesta para el cliente
	        ResponseMessage response;
	        if (isSaved) {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Product created successfully with code: " + newProduct.getCode())
	                .build();
	        } else {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Failed to create or update the product.")
	                .build();
	        }

	        // Enviar la respuesta al cliente
	        responseObserver.onNext(response);
	        responseObserver.onCompleted();

	    } catch (Exception e) {
	        // Manejar errores inesperados
	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException()
	        );
	    }
	}

	}


