package org.acme.hellokubernetes;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageService {
    
    public String getMessage() {
        System.out.println("Get Message Remote call");
        final RestTemplate messageServiceGateway = new RestTemplate();
        return messageServiceGateway.getForEntity("http://localhost:8090", String.class).getBody();
    }

}
