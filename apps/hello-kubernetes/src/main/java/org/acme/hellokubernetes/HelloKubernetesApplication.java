package org.acme.hellokubernetes;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@org.springframework.retry.annotation.EnableRetry
@SpringBootApplication
public class HelloKubernetesApplication {

	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            //.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(1)).build())
            .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(50).ringBufferSizeInClosedState(5).build())
            .build());
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloKubernetesApplication.class, args);
	}

}
