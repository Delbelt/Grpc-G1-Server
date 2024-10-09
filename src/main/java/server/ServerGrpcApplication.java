package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ServerGrpcApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(ServerGrpcApplication.class, args);	
//		ApplicationContext context = SpringApplication.run(ServerGrpcApplication.class, args);	
		
//		String state = "SOLICITADA";
//		
//		IOrderProcessingService processPurchaseOrder = context.getBean(IOrderProcessingService.class);
//		
//		processPurchaseOrder.processPurchaseOrders(state);		
	}
}
