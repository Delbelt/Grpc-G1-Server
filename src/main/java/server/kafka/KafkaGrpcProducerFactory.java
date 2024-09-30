package server.kafka;

import java.util.Map;

import org.apache.kafka.clients.producer.Producer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

public class KafkaGrpcProducerFactory<K, V> extends DefaultKafkaProducerFactory<K, V> {

	public KafkaGrpcProducerFactory(Map<String, Object> configs) {
		
		super(configs);
	}

	@Override
	protected Producer<K, V> createKafkaProducer() {
		
		return super.createKafkaProducer();
	}
}
