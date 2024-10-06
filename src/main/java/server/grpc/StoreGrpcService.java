package server.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.StoreGrpcServiceGrpc.StoreGrpcServiceImplBase;
import server.StoreProto.AssignProductRequest;
import server.StoreProto.AssignUserRequest;
import server.StoreProto.ChangeStoreStateRequest;
import server.StoreProto.ProductGrpc;
import server.StoreProto.RemoveProductRequest;
import server.StoreProto.RemoveProductResponse;
import server.StoreProto.RemoveUserRequest;
import server.StoreProto.RemoveUserResponse;
import server.StoreProto.RequestCode;
import server.StoreProto.StockGrpc;
import server.StoreProto.StoreGrpc;
import server.StoreProto.StoreListResponse;
import server.StoreProto.StoreStateRequest;
import server.StoreProto.UserGrpc;
import server.entities.Product;
import server.entities.Stock;
import server.entities.Store;
import server.entities.User;
import server.security.GrpcSecurityConfig.RoleAuth;
import server.security.Roles;
import server.services.IProductService;
import server.services.IStockService;
import server.services.IStoreService;
import server.services.IUserService;

@GrpcService
public class StoreGrpcService extends StoreGrpcServiceImplBase{
	@Autowired
    private IStoreService storeService;

	@Autowired
    private IProductService productService;
    
    @Autowired
    private IStockService stockService;
    
    @Autowired
    private IUserService userService;
    
    
    
	private List<StockGrpc> convertStocksToGrpc(List<Stock> list) {
        List<StockGrpc> stockGrpcList = new ArrayList<>();
        for (Stock stock : list) {
            StockGrpc stockGrpc = StockGrpc.newBuilder()
                .setCode(stock.getCode())
                .setProduct(ProductGrpc.newBuilder()
                	.setCode(stock.getProduct().getCode())
                    .setName(stock.getProduct().getName())
                    .setSize(stock.getProduct().getSize())
                    .setPhoto(stock.getProduct().getPhoto())
                    .setColor(stock.getProduct().getColor())
                    .build())
                .setQuantity(stock.getQuantity())
                .build();
            stockGrpcList.add(stockGrpc);
        }
        return stockGrpcList;
    }

    private List<UserGrpc> convertUsersToGrpc(Set<User> set) {
        List<UserGrpc> userGrpcList = new ArrayList<>();
        for (User user : set) {
            UserGrpc userGrpc = UserGrpc.newBuilder()
                .setUserName(user.getUserName())
                .setActive(user.isActive())
                .build();
            userGrpcList.add(userGrpc);
        }
        return userGrpcList;
    }
	
    private StoreGrpc convertStoreToGrpc(Store store) {
        // Reutiliza los métodos existentes para convertir stocks y usuarios
        List<StockGrpc> stockGrpcList = convertStocksToGrpc(store.getStocks());
        List<UserGrpc> userGrpcList = convertUsersToGrpc(store.getUsers());

        // Construir y retornar el StoreGrpc
        return StoreGrpc.newBuilder()
                .setCode(store.getCode())
                .setAddress(store.getAddress())
                .setCity(store.getCity())
                .setProvince(store.getProvince())
                .setActive(store.isActive())
                .addAllStocks(stockGrpcList)  // Agregar la lista de stocks
                .addAllUsers(userGrpcList)    // Agregar la lista de usuarios
                .build();
    }

    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void getStoreGrpc(RequestCode request, StreamObserver<StoreGrpc> responseObserver) {

        try {
            
            var store = storeService.getStoreByCode(request.getCode());

            if (store == null) {
            	String messageError = "Store with code " + request.getCode() + " not found";
				
				throw new NoSuchElementException(messageError);
                
            }

            
            StoreGrpc storeGrpc = StoreGrpc.newBuilder()
                    .setCode(store.getCode())
                    .setAddress(store.getAddress())
                    .setCity(store.getCity())           
                    .setProvince(store.getProvince())   
                    .setActive(store.isActive())
                    .addAllStocks(convertStocksToGrpc(store.getStocks())) 
                    .addAllUsers(convertUsersToGrpc(store.getUsers()))   
                    .build();

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(storeGrpc);
            responseObserver.onCompleted();

        } catch (NoSuchElementException e) {			
        	
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
    
    
    // Método para obtener tiendas por estado (habilitadas o deshabilitadas)
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})  
    public void getStoresByState(StoreStateRequest request, StreamObserver<StoreListResponse> responseObserver) {
        try {
            
            boolean active = request.getActive();

            
            List<Store> stores = storeService.getStoresByState(active);

            
            if (stores.isEmpty()) {
                throw new NoSuchElementException("No stores found for the requested state.");
            }

            
            StoreListResponse.Builder responseBuilder = StoreListResponse.newBuilder();
            for (Store store : stores) {
                StoreGrpc storeGrpc = StoreGrpc.newBuilder()
                        .setCode(store.getCode())
                        .setAddress(store.getAddress())
                        .setCity(store.getCity())
                        .setProvince(store.getProvince())
                        .setActive(store.isActive())
                        .addAllStocks(convertStocksToGrpc(store.getStocks())) 
                        .addAllUsers(convertUsersToGrpc(store.getUsers()))  
                        .build();
                responseBuilder.addStores(storeGrpc);
            }

            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }
    
    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void createStore(StoreGrpc request, StreamObserver<StoreGrpc> responseObserver) {
        try {
            
            Store store = new Store();
            store.setCode(request.getCode());
            store.setAddress(request.getAddress());
            store.setCity(request.getCity());
            store.setProvince(request.getProvince());
            store.setActive(request.getActive());

            Store createdStore = storeService.createStore(store);

            StoreGrpc response = StoreGrpc.newBuilder()
                .setCode(createdStore.getCode())
                .setAddress(createdStore.getAddress())
                .setCity(createdStore.getCity())
                .setProvince(createdStore.getProvince())
                .setActive(createdStore.isActive())
                .addAllStocks(convertStocksToGrpc(store.getStocks())) 
                .addAllUsers(convertUsersToGrpc(store.getUsers()))   
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }  catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }
    
    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void changeStoreState(ChangeStoreStateRequest request, StreamObserver<StoreGrpc> responseObserver) {
        try {
            Store updatedStore = storeService.changeStoreState(request.getCode(), request.getActive());


            StoreGrpc response = StoreGrpc.newBuilder()
                .setCode(updatedStore.getCode())
                .setAddress(updatedStore.getAddress())
                .setCity(updatedStore.getCity())
                .setProvince(updatedStore.getProvince())
                .setActive(updatedStore.isActive())
                .addAllStocks(convertStocksToGrpc(updatedStore.getStocks())) 
                .addAllUsers(convertUsersToGrpc(updatedStore.getUsers()))   
                .build();


            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }  catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }
    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void assignProductToStore(AssignProductRequest request, StreamObserver<StockGrpc> responseObserver) {
        try {
            
            Product product = productService.findByCode(request.getProductCode());
            if (product == null) {
                throw new NoSuchElementException("Product not found with code: " + request.getProductCode());
            }

           
            Store store = storeService.getStoreByCode(request.getStoreCode()); 
            if (store == null) {
                throw new NoSuchElementException("Store not found with code: " + request.getStoreCode());
            }

            
            if (stockService.stockExists(request.getProductCode(), request.getStoreCode())) {
                String messageError = "Stock already exists for product " + request.getProductCode() + " and store " + request.getStoreCode();
                throw new NoSuchElementException(messageError);
            }

            
            Stock newStock = stockService.createStock(request.getStoreCode(), request.getProductCode(), 0); 

            
            ProductGrpc productGrpc = ProductGrpc.newBuilder()
                    .setCode(newStock.getProduct().getCode())
                    .setName(newStock.getProduct().getName())
                    .setSize(newStock.getProduct().getSize())
                    .setPhoto(newStock.getProduct().getPhoto())
                    .setColor(newStock.getProduct().getColor())
                    .build();
            StockGrpc stockGrpc = StockGrpc.newBuilder()
                    .setCode(newStock.getCode())
                    .setProduct(productGrpc)
                    .build();

            
            responseObserver.onNext(stockGrpc);
            responseObserver.onCompleted();
            
        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }
    
    @Override
    @RoleAuth({ Roles.ADMIN })
    public void assignUserToStore(AssignUserRequest request, StreamObserver<StoreGrpc> responseObserver) {
        try {
            
            Store store = storeService.getStoreByCode(request.getStoreCode());
            if (store == null) {
                throw new NoSuchElementException("Store not found with code: " + request.getStoreCode());
            }

           
            User user = userService.findById(request.getUserId());
            if (user == null) {
                throw new NoSuchElementException("User not found with ID: " + request.getUserId());
            }

            
            store.getUsers().add(user);

            
            storeService.createStore(store);

            
            StoreGrpc storeGrpc = StoreGrpc.newBuilder()
                .setCode(store.getCode())
                .setAddress(store.getAddress())
                .setCity(store.getCity())
                .setProvince(store.getProvince())
                .setActive(store.isActive())
                .addAllUsers(convertUsersToGrpc(store.getUsers())) 
                .addAllStocks(convertStocksToGrpc(store.getStocks())) 
                .build();

            responseObserver.onNext(storeGrpc);
            responseObserver.onCompleted();
        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }
    
 
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void removeProductFromStore(RemoveProductRequest request, StreamObserver<RemoveProductResponse> responseObserver) {
        try {
            
            Store store = storeService.getStoreByCode(request.getStoreCode());
            if (store == null) {
                throw new NoSuchElementException("Store not found with code: " + request.getStoreCode());
            }

            
            Stock stock = stockService.findByStoreAndProduct(request.getStoreCode(), request.getProductCode());
            if (stock == null) {
                throw new NoSuchElementException("Stock not found for product " + request.getProductCode() + " in store " + request.getStoreCode());
            }

            
            stockService.remove(stock.getCode());

            
            String message = "Product with code " + request.getProductCode() + " was successfully removed from store " + request.getStoreCode();

            
            RemoveProductResponse response = RemoveProductResponse.newBuilder()
                    .setMessage(message)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }

    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void removeUserFromStore(RemoveUserRequest request, StreamObserver<RemoveUserResponse> responseObserver) {
        try {
            
            Store store = storeService.getStoreByCode(request.getStoreCode());
            if (store == null) {
                throw new NoSuchElementException("Store not found with code: " + request.getStoreCode());
            }

           
            User user = userService.findById(request.getUserId());
            if (user == null || !store.getUsers().contains(user)) {
                throw new NoSuchElementException("User with ID " + request.getUserId() + " not found in store " + request.getStoreCode());
            }

            
            store.getUsers().remove(user);
            storeService.updateStore(store);  

            
            String message = "User with ID " + request.getUserId() + " was successfully removed from store " + request.getStoreCode();

            
            RemoveUserResponse response = RemoveUserResponse.newBuilder()
                    .setMessage(message)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchElementException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error: " + e.getMessage()).asRuntimeException());
        }
    }




    
    
    


    

    
 
}
