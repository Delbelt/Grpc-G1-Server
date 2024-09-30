package server.kafka;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, String message) throws InterruptedException, ExecutionException {
		kafkaTemplate.send(topic, message).get();
	}
}
