package server.grpc;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import server.KafkaGrpcServiceGrpc.KafkaGrpcServiceImplBase;
import server.kafkaProto.SendMessageRequest;
import server.kafkaProto.SendMessageResponse;
import server.kafka.KafkaMessageProducer;

@GrpcService
public class KafkaGrpcService extends KafkaGrpcServiceImplBase {	

	@Autowired
	private KafkaMessageProducer kafkaTemplate;

	@Override
	public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
		
		String topic = request.getTopic();
		String message = request.getMessage();

		try {
			
			kafkaTemplate.sendMessage(topic, message);
			
			SendMessageResponse response = 
					SendMessageResponse
					.newBuilder()
					.setMessage("Completed")
					.setSuccess(true)					
					.build();
			
			responseObserver.onNext(response);
		}
		
		catch (Exception e) {
			
			System.out.println("********************************");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getStackTrace());
			System.out.println("********************************");
			
			SendMessageResponse response =
					SendMessageResponse
					.newBuilder()
					.setMessage(e.getMessage())
					.setSuccess(false)					
					.build();
			
			responseObserver.onNext(response);
		}
		
		finally {
			
			responseObserver.onCompleted();
		}
	}

}
