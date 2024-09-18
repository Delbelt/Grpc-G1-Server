package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.StoreGrpcServiceGrpc.StoreGrpcServiceImplBase;
import server.StoreProto.RequestCode;
import server.StoreProto.StoreGrpc;
import server.services.IStoreService;

@GrpcService
public class StoreGrpcService extends StoreGrpcServiceImplBase{
	@Autowired
    private IStoreService storeService;

    @Override
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
        } catch (Exception e) {
            String errorMessage = "Internal server error: " + e.getMessage();
            responseObserver
                .onError(Status.INTERNAL.withDescription(errorMessage)
                .asRuntimeException());
        }
    }
}
