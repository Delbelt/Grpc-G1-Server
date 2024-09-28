package server.grpc;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.StoreGrpcServiceGrpc.StoreGrpcServiceImplBase;
import server.StoreProto.RequestCode;
import server.StoreProto.StoreGrpc;
import server.StoreProto.StoreListResponse;
import server.StoreProto.StoreStateRequest;
import server.entities.Store;
import server.security.GrpcSecurityConfig.RoleAuth;
import server.security.Roles;
import server.services.IStoreService;

@GrpcService
public class StoreGrpcService extends StoreGrpcServiceImplBase{
	@Autowired
    private IStoreService storeService;

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
    
    
 
}
