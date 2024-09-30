package server.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
	
	private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

	@Bean
	public ProducerFactory<String, String> producerFactory() {

		Map<String, Object> configProps = new HashMap<>();
		
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId());
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaURL());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getRetry());
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProperties.getBackoffMs());
        configProps.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, kafkaProperties.getBackoffMax());
        configProps.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, kafkaProperties.getBackoffMax());
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, kafkaProperties.getTimeOutMs());
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.getBackoffMax());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.getLingerMs());
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

		return new KafkaGrpcProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {

		return new KafkaTemplate<>(producerFactory());
	}
}
