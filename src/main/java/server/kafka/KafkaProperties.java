package server.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Configuration
@Data
public class KafkaProperties {
	
    @Value("${kafka.url}")
    private String kafkaURL;
    
    @Value("${kafka.clientId}")
    private String clientId;

    @Value("${kafka.retry}")
    private int retry;

    @Value("${kafka.backoffMax}")
    private int backoffMax;

    @Value("${kafka.lingerMs}")
    private int lingerMs;

    private int backoffMs;
    
    private int timeOutMs;
    
    @PostConstruct
    public void init() {
        this.backoffMs = backoffMax / retry; 
        this.timeOutMs = backoffMax + lingerMs;
    }
}
