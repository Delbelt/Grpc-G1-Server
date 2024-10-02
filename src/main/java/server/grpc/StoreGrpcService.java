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
import server.StoreProto.ChangeStoreStateRequest;
import server.StoreProto.ProductGrpc;
import server.StoreProto.RequestCode;
import server.StoreProto.StockGrpc;
import server.StoreProto.StoreGrpc;
import server.StoreProto.StoreListResponse;
import server.StoreProto.StoreStateRequest;
import server.StoreProto.UserGrpc;
import server.entities.Stock;
import server.entities.Store;
import server.entities.User;
import server.security.GrpcSecurityConfig.RoleAuth;
import server.security.Roles;
import server.services.IStoreService;

@GrpcService
public class StoreGrpcService extends StoreGrpcServiceImplBase{
	@Autowired
    private IStoreService storeService;

	private List<StockGrpc> convertStocksToGrpc(List<Stock> list) {
        List<StockGrpc> stockGrpcList = new ArrayList<>();
        for (Stock stock : list) {
            StockGrpc stockGrpc = StockGrpc.newBuilder()
                .setCode(stock.getCode())
                .setProduct(ProductGrpc.newBuilder()
                    .setName(stock.getProduct().getName())
                    .setSize(stock.getProduct().getSize())
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
	
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void getStoreGrpc(RequestCode request, StreamObserver<StoreGrpc> responseObserver) {

        try {
            // Llamar al servicio para obtener la tienda por Code
            var store = storeService.getStoreByCode(request.getCode());

            if (store == null) {
            	String messageError = "Store with code " + request.getCode() + " not found";
				
				throw new NoSuchElementException(messageError);
                
            }

            // Construir la respuesta de gRPC con los datos de la tienda
            StoreGrpc storeGrpc = StoreGrpc.newBuilder()
                    .setCode(store.getCode())
                    .setAddress(store.getAddress())
                    .setCity(store.getCity())           
                    .setProvince(store.getProvince())   
                    .setActive(store.isActive())
                    .addAllStocks(convertStocksToGrpc(store.getStocks())) // Convertir la lista de stocks a gRPC
                    .addAllUsers(convertUsersToGrpc(store.getUsers()))   // Convertir la lista de usuarios a gRPC
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
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})  // Proteger el método con roles
    public void getStoresByState(StoreStateRequest request, StreamObserver<StoreListResponse> responseObserver) {
        try {
            // Obtener el estado de habilitado/deshabilitado
            boolean active = request.getActive();

            // Llamar al servicio para obtener las tiendas filtradas por estado
            List<Store> stores = storeService.getStoresByState(active);

            // Verificar si se encontraron tiendas
            if (stores.isEmpty()) {
                throw new NoSuchElementException("No stores found for the requested state.");
            }

            // Construir la respuesta con la lista de tiendas
            StoreListResponse.Builder responseBuilder = StoreListResponse.newBuilder();
            for (Store store : stores) {
                StoreGrpc storeGrpc = StoreGrpc.newBuilder()
                        .setCode(store.getCode())
                        .setAddress(store.getAddress())
                        .setCity(store.getCity())
                        .setProvince(store.getProvince())
                        .setActive(store.isActive())
                        .addAllStocks(convertStocksToGrpc(store.getStocks())) // Convertir la lista de stocks a gRPC
                        .addAllUsers(convertUsersToGrpc(store.getUsers()))   // Convertir la lista de usuarios a gRPC
                        .build();
                responseBuilder.addStores(storeGrpc);
            }

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (NoSuchElementException e) {
            // Si no se encontraron tiendas, devolver un error NOT_FOUND
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            // Para cualquier otro error, devolver un error INTERNAL
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }
    
    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void createStore(StoreGrpc request, StreamObserver<StoreGrpc> responseObserver) {
        try {
            // Convertir el mensaje gRPC a la entidad Store
            Store store = new Store();
            store.setCode(request.getCode());
            store.setAddress(request.getAddress());
            store.setCity(request.getCity());
            store.setProvince(request.getProvince());
            store.setActive(request.getActive());

            // Crear la tienda
            Store createdStore = storeService.createStore(store);

            // Construir la respuesta gRPC con los datos de la tienda creada
            StoreGrpc response = StoreGrpc.newBuilder()
                .setCode(createdStore.getCode())
                .setAddress(createdStore.getAddress())
                .setCity(createdStore.getCity())
                .setProvince(createdStore.getProvince())
                .setActive(createdStore.isActive())
                .addAllStocks(convertStocksToGrpc(store.getStocks())) // Convertir la lista de stocks a gRPC
                .addAllUsers(convertUsersToGrpc(store.getUsers()))   // Convertir la lista de usuarios a gRPC
                .build();

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }  catch (NoSuchElementException e) {
            // Si no se encontraron tiendas, devolver un error NOT_FOUND
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            // Para cualquier otro error, devolver un error INTERNAL
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }
    
    
    @Override
    @RoleAuth({Roles.ADMIN, Roles.CENTRAL})
    public void changeStoreState(ChangeStoreStateRequest request, StreamObserver<StoreGrpc> responseObserver) {
        try {
            // Cambiar el estado de la tienda (habilitar o deshabilitar)
            Store updatedStore = storeService.changeStoreState(request.getCode(), request.getActive());

            // Construir la respuesta gRPC con los datos actualizados
            StoreGrpc response = StoreGrpc.newBuilder()
                .setCode(updatedStore.getCode())
                .setAddress(updatedStore.getAddress())
                .setCity(updatedStore.getCity())
                .setProvince(updatedStore.getProvince())
                .setActive(updatedStore.isActive())
                .addAllStocks(convertStocksToGrpc(updatedStore.getStocks())) // Agregar stocks
                .addAllUsers(convertUsersToGrpc(updatedStore.getUsers()))   // Agregar usuarios
                .build();

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }  catch (NoSuchElementException e) {
            // Si no se encontraron tiendas, devolver un error NOT_FOUND
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            // Para cualquier otro error, devolver un error INTERNAL
            String messageError = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(messageError).asRuntimeException());
        }
    }


    
    
 
}
