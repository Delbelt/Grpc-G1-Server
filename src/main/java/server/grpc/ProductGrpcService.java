package server.grpc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.ProductGrpcServiceGrpc.ProductGrpcServiceImplBase;
import server.ProductProto.Empty;
import server.ProductProto.ProductFilterRequest;
import server.ProductProto.ProductGrpc;
import server.ProductProto.ProductList;
import server.ProductProto.RequestActive;
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
			        .setPhoto(response.getPhoto())  // Usa 'response' en lugar de 'product'
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
    public void getAllProductsActive(RequestActive request, StreamObserver<ProductList> responseObserver) {
		
        try {
        	
            var products = services.findAllByActive(request.getActive());

            ProductList.Builder productListBuilder = ProductList.newBuilder();
            
            for (Product product : products) {
            	
                ProductGrpc productGrpc = 
                	ProductGrpc.newBuilder()
                    .setCode(product.getCode())
                    .setName(product.getName())
                    .setSize(product.getSize())
                    .setPhoto(product.getPhoto())
                    .setColor(product.getColor())
                    .setActive(product.isActive())
                    .build();
                
                productListBuilder.addProducts(productGrpc);
            }

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

	        Product newProduct = new Product();
	        newProduct.setCode(request.getCode());
	        newProduct.setName(request.getName());
	        newProduct.setSize(request.getSize());
	        newProduct.setPhoto(request.getPhoto());
	        newProduct.setColor(request.getColor());
	        newProduct.setActive(request.getActive());

	        
	        boolean isSaved = services.insertOrUpdate(newProduct);

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

	      
	        responseObserver.onNext(response);
	        responseObserver.onCompleted();

	    } catch (Exception e) {
	        
	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException()
	        );
	    }
	}
	
	@Override
	public void getProductsByFilter(ProductFilterRequest request, StreamObserver<ProductList> responseObserver) {
	    try {
	   
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
	                .setPhoto(product.getPhoto())
	                .build();
	            productListBuilder.addProducts(productGrpc);
	        }

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

	        Product product = services.findByCode(request.getCode());

	        if (product == null) {

	            responseObserver.onError(
	                Status.NOT_FOUND
	                .withDescription("Product with code " + request.getCode() + " not found.")
	                .asRuntimeException());
	            return;
	        }

	        boolean isDeleted = services.deleteByCode(product.getCode());

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


	        responseObserver.onNext(response);
	        responseObserver.onCompleted();
	    } catch (Exception e) {

	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException());
	    }
	}
	
	@Override
	public void updateProduct(ProductGrpc request, StreamObserver<ResponseMessage> responseObserver) {
	    try {

	        Product existingProduct = new Product();
	        existingProduct.setCode(request.getCode());
	        existingProduct.setName(request.getName());
	        existingProduct.setSize(request.getSize());
	        existingProduct.setPhoto(request.getPhoto());
	        existingProduct.setColor(request.getColor());
	        existingProduct.setActive(request.getActive());

	
	        boolean isUpdated = services.updateProduct(existingProduct);

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

	        responseObserver.onNext(response);
	        responseObserver.onCompleted();

	    } catch (Exception e) {
	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException()
	        );
	    }
	}
	
	@Override
	public void modifyProductActive(RequestId request, StreamObserver<ResponseMessage> responseObserver) {
	    try {
	        
	        Product existingProduct = services.findByCode(request.getCode());

	        if (existingProduct == null) {
	            responseObserver.onError(
	                Status.NOT_FOUND.withDescription("Product not found with code: " + request.getCode())
	                .asRuntimeException());
	            return;
	        }
	      
	        existingProduct.setActive(!existingProduct.isActive()); 

	        boolean isUpdated = services.updateProduct(existingProduct);

	        ResponseMessage response;
	        if (isUpdated) {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Product modified successfully with code: " + existingProduct.getCode())
	                .build();
	        } else {
	            response = ResponseMessage.newBuilder()
	                .setMessage("Failed to modify the product. Product not found with code: " + existingProduct.getCode())
	                .build();
	        }

	        responseObserver.onNext(response);
	        responseObserver.onCompleted();

	    } catch (Exception e) {

	        responseObserver.onError(
	            Status.INTERNAL.withDescription("Internal server error: " + e.getMessage())
	            .asRuntimeException());
	    }
	}


		
	}


