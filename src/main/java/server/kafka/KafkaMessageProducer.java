package server.kafka;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaMessageProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, String message) throws InterruptedException, ExecutionException {
		kafkaTemplate.send(topic, message).get();	
	}

	public void sendObjectMessage(String topic, Object message) throws InterruptedException, ExecutionException {
		
		try {
			
            String messageJson = mapper.writeValueAsString(message);
   
            kafkaTemplate.send(topic, messageJson).get();
        } 
		
		catch (Exception e) {
			
            System.err.println("Error al serializar el mensaje: " + e.getMessage());
        }
	}
}
