package org.acme.hellokubernetes;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@RestController
public class HelloController {
    
    @Autowired
    private HelloConfig helloConfig;

    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable("name") String name) {
      return helloConfig.getMessage() + " " + name;
    }

    private CircuitBreaker messageCircuitBreaker;

    private Set<String> names = new HashSet<>();

    public HelloController(MeterRegistry registry, CircuitBreakerFactory messageCircuitBreakerFactory) {
      this.messageCircuitBreaker = messageCircuitBreakerFactory.create("message");
      registry.gaugeCollectionSize("names.size", Tags.empty(), names);
    }

    @Autowired
    MessageService messageService;

    @GetMapping("/hello")
    String hello() {
      return this.messageCircuitBreaker.run(() -> 
          "This is Spring calling a " + messageService.getMessage()
            , throwable -> "Default"
            );
    }

    @GetMapping("/hello/{name}")
    String helloWithName(@PathVariable("name") String name) {
      names.add(name);
      return "Hello World " + name;
    }

    @GetMapping("/hello/error")
    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 1000))
    String helloWithException() {
      System.out.println("Method with error");
      throw new IllegalArgumentException("Error");
    }

}
