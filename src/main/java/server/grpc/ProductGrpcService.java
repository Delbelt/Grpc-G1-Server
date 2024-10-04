package server.grpc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.ProductGrpcServiceGrpc.ProductGrpcServiceImplBase;
import server.ProductProto.Empty;
import server.ProductProto.ProductFilterRequest;
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
	
	@Override
	public void getProductsByFilter(ProductFilterRequest request, StreamObserver<ProductList> responseObserver) {
	    try {
	        // Obtener todos los productos desde el servicio
	        List<Product> allProducts = services.findAll();

	        // Aplicar filtros a los productos en memoria
	        List<Product> filteredProducts = allProducts.stream()
	                .filter(product -> (request.getCode().isEmpty() || product.getCode().equals(request.getCode())))
	                .filter(product -> (request.getName().isEmpty() || product.getName().equals(request.getName())))
	                .filter(product -> (request.getSize().isEmpty() || product.getSize().equals(request.getSize())))
	                .filter(product -> (request.getColor().isEmpty() || product.getColor().equals(request.getColor())))
	                .collect(Collectors.toList());

	        // Convertir los productos filtrados a ProductGrpc
	        ProductList.Builder productListBuilder = ProductList.newBuilder();
	        for (Product product : filteredProducts) {
	            ProductGrpc productGrpc = ProductGrpc.newBuilder()
	                .setCode(product.getCode())
	                .setName(product.getName() != null ? product.getName() : "")
	                .setSize(product.getSize() != null ? product.getSize() : "")
	                .setColor(product.getColor() != null ? product.getColor() : "")
	                .setActive(product.isActive())
	                .setPhoto(ByteString.copyFrom(product.getPhoto() != null ? product.getPhoto() : new byte[0]))
	                .build();
	            productListBuilder.addProducts(productGrpc);
	        }

	        // Enviar la lista de productos filtrados al cliente
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
	public void deleteProduct(RequestId request, StreamObserver<ResponseMessage> responseObserver) {
	    try {
	        // Buscar el producto por el código
	        Product product = services.findByCode(request.getCode());

	        if (product == null) {
	            // Si el producto no se encuentra, devolver un error de "no encontrado"
	            responseObserver.onError(
	                Status.NOT_FOUND
	                .withDescription("Product with code " + request.getCode() + " not found.")
	                .asRuntimeException());
	            return;
	        }

	        // Eliminar el producto usando el servicio
	        boolean isDeleted = services.deleteByCode(product.getCode());

	        // Preparar el mensaje de respuesta
	        ResponseMessage response;
	        if (isDeleted) {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Product with code " + request.getCode() + " deleted successfully.")
	                .build();
	        } else {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Failed to delete product with code " + request.getCode() + ".")
	                .build();
	        }

	        // Enviar la respuesta al cliente
	        responseObserver.onNext(response);
	        responseObserver.onCompleted();
	    } catch (Exception e) {
	        // Manejar errores inesperados
	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException());
	    }
	}
	
	@Override
	public void updateProduct(ProductGrpc request, StreamObserver<ResponseMessage> responseObserver) {
	    try {
	        // Crear una entidad Product a partir de los datos recibidos
	        Product existingProduct = new Product();
	        existingProduct.setCode(request.getCode());
	        existingProduct.setName(request.getName());
	        existingProduct.setSize(request.getSize());
	        existingProduct.setPhoto(request.getPhoto().toByteArray()); // Convertir ByteString a byte[]
	        existingProduct.setColor(request.getColor());
	        existingProduct.setActive(request.getActive());

	        // Usar el servicio para actualizar el producto
	        boolean isUpdated = services.updateProduct(existingProduct);

	        // Preparar la respuesta para el cliente
	        ResponseMessage response;
	        if (isUpdated) {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Product updated successfully with code: " + existingProduct.getCode())
	                .build();
	        } else {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Failed to update the product. Product not found with code: " + existingProduct.getCode())
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


