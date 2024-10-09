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
import stock.Stock.CreateStockRequest;
import stock.Stock.Empty;
import stock.Stock.GetStockByIdRequest;
import stock.Stock.GetStockByProductRequest;
import stock.Stock.GetStockByStoreRequest;
import stock.Stock.StockGrpc;
import stock.Stock.StockList;
import stock.StockGrpcServiceGrpc.StockGrpcServiceImplBase;

@GrpcService
public class StockGrpcService extends StockGrpcServiceImplBase {

	@Autowired
	private IStockService stockService;

	@Override
	@RoleAuth({ Roles.ADMIN })
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
					.setPhoto(stock.getProduct().getPhoto())
					.setColor(stock.getProduct().getColor()).build();
			// Construir la respuesta de StockGrpc, solo con los códigos
			StockGrpc stockGrpc = StockGrpc.newBuilder().setCode(stock.getCode()) // Código del stock
					.setStoreCode(stock.getStore().getCode()) // Solo código del store
					.setProduct(productGrpc) // Objeto product obtenido
					.setQuantity(stock.getQuantity()) // Cantidad
					.build();

			// Enviar la respuesta y completar la comunicación
			responseObserver.onNext(stockGrpc);
			responseObserver.onCompleted();

		} catch (NoSuchElementException e) {
			responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	// Metodo para obtener todos los Stocks
	@Override
	@RoleAuth({ Roles.ADMIN })
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
						.setPhoto(stock.getProduct().getPhoto())
						.setColor(stock.getProduct().getColor()).build();

				return StockGrpc.newBuilder().setCode(stock.getCode()) // Código del stock
						.setStoreCode(stock.getStore().getCode()) // Solo código del store
						.setProduct(productGrpc) // Objeto product obtenido
						.setQuantity(stock.getQuantity()) // Cantidad
						.build();
			}).collect(Collectors.toList());

			// Construir la respuesta de StockList
			StockList stockList = StockList.newBuilder().addAllStocks(grpcStocks).build();

			// Enviar la respuesta y completar la comunicación
			responseObserver.onNext(stockList);
			responseObserver.onCompleted();

		} catch (NoSuchElementException e) {
			responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	// Metodo para obtener todos los Stocks Disponibles
	@Override
	@RoleAuth({ Roles.ADMIN })
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
						.setPhoto(stock.getProduct().getPhoto())
						.setColor(stock.getProduct().getColor()).build();

				return StockGrpc.newBuilder().setCode(stock.getCode()).setStoreCode(stock.getStore().getCode())
						.setProduct(productGrpc).setQuantity(stock.getQuantity()).build();
			}).collect(Collectors.toList());

			// Construir la respuesta
			StockList stockList = StockList.newBuilder().addAllStocks(grpcStocks).build();

			// Enviar la respuesta
			responseObserver.onNext(stockList);
			responseObserver.onCompleted();
		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	@Override
	@RoleAuth({ Roles.ADMIN })
	public void getUnavailableStocks(Empty request, StreamObserver<StockList> responseObserver) {
		try {
			// Obtener stocks no disponibles desde el servicio de stock
			List<Stock> unavailableStocks = stockService.findUnavailableStocks();

			// Construir la lista de stocks no disponibles para la respuesta gRPC
			StockList.Builder stockListBuilder = StockList.newBuilder();

			for (Stock stock : unavailableStocks) {
				ProductGrpc productGrpc = ProductGrpc.newBuilder().setCode(stock.getProduct().getCode())
						.setName(stock.getProduct().getName())
						.setSize(stock.getProduct().getSize())
						.setPhoto(stock.getProduct().getPhoto())
						.setColor(stock.getProduct().getColor()).build();

				StockGrpc stockGrpc = StockGrpc.newBuilder().setCode(stock.getCode())
						.setStoreCode(stock.getStore().getCode()).setProduct(productGrpc)
						.setQuantity(stock.getQuantity()).build();

				stockListBuilder.addStocks(stockGrpc);
			}

			// Enviar la lista y completar la respuesta
			responseObserver.onNext(stockListBuilder.build());
			responseObserver.onCompleted();

		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	@Override
	@RoleAuth({ Roles.ADMIN })
	public void getStockByProduct(GetStockByProductRequest request, StreamObserver<StockList> responseObserver) {
		try {
			// Obtener los stocks asociados al producto desde el servicio de stock
			List<Stock> stocks = stockService.getStockByProduct(request.getProductCode());

			if (stocks.isEmpty()) {
				String messageError = "No stocks found for product " + request.getProductCode();
				throw new NoSuchElementException(messageError);
			}

			// Convertir la lista de entidades Stock a una lista de StockGrpc
			List<StockGrpc> grpcStocks = stocks.stream().map(stock -> {
				ProductGrpc productGrpc = ProductGrpc.newBuilder().setCode(stock.getProduct().getCode())
						.setName(stock.getProduct().getName())
						.setSize(stock.getProduct().getSize())
						.setPhoto(stock.getProduct().getPhoto())
						.setColor(stock.getProduct().getColor()).build();

				return StockGrpc.newBuilder().setCode(stock.getCode()).setStoreCode(stock.getStore().getCode())
						.setProduct(productGrpc).setQuantity(stock.getQuantity()).build();
			}).collect(Collectors.toList());

			// Construir la respuesta de StockList
			StockList stockList = StockList.newBuilder().addAllStocks(grpcStocks).build();

			// Enviar la respuesta y completar la comunicación
			responseObserver.onNext(stockList);
			responseObserver.onCompleted();

		} catch (NoSuchElementException e) {
			responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	@Override
	@RoleAuth({ Roles.ADMIN })
	public void getStockByStore(GetStockByStoreRequest request, StreamObserver<StockList> responseObserver) {
		try {
			// Obtener stocks por código de tienda
			List<Stock> stocks = stockService.getStockByStore(request.getStoreCode());

			if (stocks.isEmpty()) {
				String messageError = "No stocks found for store " + request.getStoreCode();
				throw new NoSuchElementException(messageError);
			}

			// Convertir la lista de entidades Stock a una lista de StockGrpc
			List<StockGrpc> grpcStocks = stocks.stream().map(stock -> {
				ProductGrpc productGrpc = ProductGrpc.newBuilder().setCode(stock.getProduct().getCode())
						.setName(stock.getProduct().getName())
						.setSize(stock.getProduct().getSize())
						.setPhoto(stock.getProduct().getPhoto())
						.setColor(stock.getProduct().getColor()).build();

				return StockGrpc.newBuilder().setCode(stock.getCode()) // Código del stock
						.setStoreCode(stock.getStore().getCode()) // Solo código del store
						.setProduct(productGrpc) // Objeto product obtenido
						.setQuantity(stock.getQuantity()) // Cantidad
						.build();
			}).collect(Collectors.toList());

			// Construir la respuesta de StockList
			StockList stockList = StockList.newBuilder().addAllStocks(grpcStocks).build();

			// Enviar la respuesta y completar la comunicación
			responseObserver.onNext(stockList);
			responseObserver.onCompleted();

		} catch (NoSuchElementException e) {
			responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
		} catch (Exception e) {
			String errorMessage = "Internal server error: " + e.getMessage();
			responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
		}
	}

	@Override
	@RoleAuth({ Roles.ADMIN })
	public void createStock(CreateStockRequest request, StreamObserver<StockGrpc> responseObserver) {
	    try {
	        // 1. Verificar si el stock ya existe
	        if (stockService.stockExists(request.getProductCode(), request.getStoreCode())) {
	            String messageError = "Stock already exists for product " + request.getProductCode() + " and store " + request.getStoreCode();
	            throw new NoSuchElementException(messageError);
	        }

	        // Crear el nuevo stock, pasando también la cantidad
	        Stock newStock = stockService.createStock(request.getStoreCode(), request.getProductCode(), request.getQuantity());

	        // Construir el producto gRPC
	        ProductGrpc productGrpc = ProductGrpc.newBuilder()
	                .setCode(newStock.getProduct().getCode())
	                .setName(newStock.getProduct().getName())
	                .setSize(newStock.getProduct().getSize())
	                .setPhoto(newStock.getProduct().getPhoto())
	                .setColor(newStock.getProduct().getColor()).build();

	        // Construir la respuesta gRPC
	        StockGrpc stockGrpc = StockGrpc.newBuilder()
	                .setCode(newStock.getCode())
	                .setStoreCode(newStock.getStore().getCode())
	                .setProduct(productGrpc)
	                .setQuantity(newStock.getQuantity()).build();

	        // Enviar la respuesta
	        responseObserver.onNext(stockGrpc);
	        responseObserver.onCompleted();
	    } catch (NoSuchElementException e) {
	        responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
	    } catch (Exception e) {
	        String errorMessage = "Internal server error: " + e.getMessage();
	        responseObserver.onError(Status.INTERNAL.withDescription(errorMessage).asRuntimeException());
	    }
	}


}