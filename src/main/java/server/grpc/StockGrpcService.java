package server.grpc;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.services.IStockService;
import stock.Stock.GetStockByIdRequest;
import stock.Stock.StockGrpc;
import stock.StockGrpcServiceGrpc.StockGrpcServiceImplBase;

@GrpcService
public class StockGrpcService extends StockGrpcServiceImplBase {

    @Autowired
    private IStockService stockService;

    @Override
    public void getStockById(GetStockByIdRequest request, StreamObserver<StockGrpc> responseObserver) {
        try {
            // Obtener stock desde el servicio de stock
            var stock = stockService.findByCode(request.getCode());

            if (stock == null) {
                String messageError = "Stock with code " + request.getCode() + " not found";
                throw new NoSuchElementException(messageError);
            }

            // Construir la respuesta de StockGrpc, solo con los códigos
            StockGrpc stockGrpc = StockGrpc.newBuilder()
                    .setCode(stock.getCode())                 // Código del stock
                    .setStoreCode(stock.getStore().getCode()) // Solo código del store
                    .setProductCode(stock.getProduct().getCode()) // Solo código del product
                    .setQuantity(stock.getQuantity())         // Cantidad
                    .build();

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(stockGrpc);
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
