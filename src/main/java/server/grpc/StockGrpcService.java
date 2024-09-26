package server.grpc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.ProductProto.ProductGrpc;
import server.entities.Stock;
import server.security.Roles;
import server.security.GrpcSecurityConfig.RoleAuth;
import server.services.IStockService;
import stock.Stock.Empty;
import stock.Stock.GetStockByIdRequest;
import stock.Stock.StockGrpc;
import stock.Stock.StockList;
import stock.StockGrpcServiceGrpc.StockGrpcServiceImplBase;

@GrpcService
public class StockGrpcService extends StockGrpcServiceImplBase {

    @Autowired
    private IStockService stockService;

    @Override
    @RoleAuth({Roles.ADMIN})
    public void getStockById(GetStockByIdRequest request, StreamObserver<StockGrpc> responseObserver) {
        try {
            // Obtener stock desde el servicio de stock
            var stock = stockService.findByCode(request.getCode());

            if (stock == null) {
                String messageError = "Stock with code " + request.getCode() + " not found";
                throw new NoSuchElementException(messageError);
            }

            // Construir la respuesta de ProductGrpc
            ProductGrpc productGrpc = ProductGrpc.newBuilder()
                    .setCode(stock.getProduct().getCode())
                    .setName(stock.getProduct().getName())
                    .setSize(stock.getProduct().getSize())
                    .setColor(stock.getProduct().getColor())
                    .build();
            // Construir la respuesta de StockGrpc, solo con los códigos
            StockGrpc stockGrpc = StockGrpc.newBuilder()
                    .setCode(stock.getCode())                 // Código del stock
                    .setStoreCode(stock.getStore().getCode()) // Solo código del store
                    .setProduct(productGrpc) // Objeto product obtenido
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
    
    // Metodo para obtener todos los Stocks
    @Override
    @RoleAuth({Roles.ADMIN})
    public void getAllStocks(Empty request, StreamObserver<StockList> responseObserver) {
        try {
            // Obtener todos los stocks desde el servicio de stock
            List<Stock> stocks = stockService.getAll();

            if (stocks.isEmpty()) {
                String messageError = "No stocks found";
                throw new NoSuchElementException(messageError);
            }

            // Convertir la lista de entidades Stock a una lista de StockGrpc
            List<StockGrpc> grpcStocks = stocks.stream().map(stock -> {
                ProductGrpc productGrpc = ProductGrpc.newBuilder()
                        .setCode(stock.getProduct().getCode())
                        .setName(stock.getProduct().getName())
                        .setSize(stock.getProduct().getSize())
                        .setColor(stock.getProduct().getColor())
                        .build();

                return StockGrpc.newBuilder()
                        .setCode(stock.getCode())                 // Código del stock
                        .setStoreCode(stock.getStore().getCode()) // Solo código del store
                        .setProduct(productGrpc) // Objeto product obtenido
                        .setQuantity(stock.getQuantity())         // Cantidad
                        .build();
            }).collect(Collectors.toList());

            // Construir la respuesta de StockList
            StockList stockList = StockList.newBuilder()
                    .addAllStocks(grpcStocks)
                    .build();

            // Enviar la respuesta y completar la comunicación
            responseObserver.onNext(stockList);
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
    
    // Metodo para obtener todos los Stocks Disponibles
    @Override
    @RoleAuth({Roles.ADMIN})
    public void getAvailableStocks(Empty request, StreamObserver<StockList> responseObserver) {
        try {
            // Obtener stocks disponibles
            List<Stock> availableStocks = stockService.findAvailableStocks();

            // Convertir a gRPC
            List<StockGrpc> grpcStocks = availableStocks.stream().map(stock -> {
                ProductGrpc productGrpc = ProductGrpc.newBuilder()
                        .setCode(stock.getProduct().getCode())
                        .setName(stock.getProduct().getName())
                        .setSize(stock.getProduct().getSize())
                        .setColor(stock.getProduct().getColor())
                        .build();

                return StockGrpc.newBuilder()
                        .setCode(stock.getCode())
                        .setStoreCode(stock.getStore().getCode())
                        .setProduct(productGrpc)
                        .setQuantity(stock.getQuantity())
                        .build();
            }).collect(Collectors.toList());

            // Construir la respuesta
            StockList stockList = StockList.newBuilder()
                    .addAllStocks(grpcStocks)
                    .build();

            // Enviar la respuesta
            responseObserver.onNext(stockList);
            responseObserver.onCompleted();
        } catch (Exception e) {
            String errorMessage = "Internal server error: " + e.getMessage();
            responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
        }
    }

}