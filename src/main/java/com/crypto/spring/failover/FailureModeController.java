package com.crypto.spring.failover;

import com.crypto.spring.model.FailureMode;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller for managing operational failure modes in our crypto client.
 * Implements our unique business logic for graceful degradation and recovery.
 * Integrates with Resilience4j for circuit breaking while adding domain-specific state management.
 */
@Component
public class FailureModeController {
    
    private final AtomicReference<FailureMode> currentMode = new AtomicReference<>(FailureMode.NORMAL);
    private final CircuitBreaker kmsCircuitBreaker;
    private final ApplicationEventPublisher eventPublisher;
    
    public FailureModeController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        
        // Our unique circuit breaker configuration
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // Our business rule: 50% failure rate triggers circuit
            .waitDurationInOpenState(Duration.ofSeconds(60)) // Our recovery window
            .slidingWindowSize(10) // Our sample size for failure calculation
            .build();
        
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.kmsCircuitBreaker = registry.circuitBreaker("kms-operations");
        
        // Our unique event handlers for circuit breaker state changes
        setupCircuitBreakerListeners();
    }
    
    /**
     * Our business operation: Get current operational mode.
     */
    public FailureMode getCurrentMode() {
        return currentMode.get();
    }
    
    /**
     * Our business operation: Transition to a new failure mode with reason tracking.
     * Implements our unique state machine logic.
     */
    public void transitionToMode(FailureMode newMode, String reason) {
        FailureMode oldMode = currentMode.getAndSet(newMode);
        
        if (oldMode != newMode) {
            // Our unique transition event
            FailureModeTransitionEvent event = new FailureModeTransitionEvent(
                oldMode,
                newMode,
                reason,
                java.time.Instant.now()
            );
            
            eventPublisher.publishEvent(event);
        }
    }
    
    /**
     * Our business operation: Execute KMS operation with circuit breaker protection.
     */
    public <T> T executeWithCircuitBreaker(java.util.function.Supplier<T> operation) {
        try {
            return kmsCircuitBreaker.executeSupplier(operation);
        } catch (Exception e) {
            // Our unique failure handling
            recordKmsFailure();
            throw e;
        }
    }
    
    /**
     * Our business operation: Record KMS operation success for health tracking.
     */
    public void recordKmsSuccess() {
        // If we're in degraded mode and KMS is working again, recover
        if (currentMode.get() == FailureMode.DEGRADED) {
            transitionToMode(FailureMode.NORMAL, "KMS operations restored");
        }
    }
    
    /**
     * Our business operation: Record KMS operation failure for degradation logic.
     */
    public void recordKmsFailure() {
        // Our unique degradation logic based on circuit breaker state
        if (kmsCircuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            if (currentMode.get() == FailureMode.NORMAL) {
                transitionToMode(FailureMode.DEGRADED, "KMS circuit breaker opened");
            }
        }
    }
    
    /**
     * Our business operation: Manually lock the system for security reasons.
     */
    public void lockOperations(String reason) {
        transitionToMode(FailureMode.LOCKED, reason);
    }
    
    /**
     * Our business operation: Unlock and return to normal operations.
     */
    public void unlockOperations(String reason) {
        transitionToMode(FailureMode.NORMAL, reason);
    }
    
    /**
     * Our business operation: Check if operations are currently allowed.
     */
    public boolean canPerformOperations() {
        return currentMode.get().isOperational();
    }
    
    /**
     * Our unique circuit breaker event handling setup.
     */
    private void setupCircuitBreakerListeners() {
        kmsCircuitBreaker.getEventPublisher()
            .onStateTransition(event -> {
                // Our unique state transition logic
                switch (event.getStateTransition()) {
                    case CLOSED_TO_OPEN:
                    case HALF_OPEN_TO_OPEN:
                        if (currentMode.get() == FailureMode.NORMAL) {
                            transitionToMode(FailureMode.DEGRADED, "Circuit breaker opened: " + event.getStateTransition());
                        }
                        break;
                        
                    case OPEN_TO_HALF_OPEN:
                        // Stay in degraded mode during testing
                        break;
                        
                    case HALF_OPEN_TO_CLOSED:
                        if (currentMode.get() == FailureMode.DEGRADED) {
                            transitionToMode(FailureMode.NORMAL, "Circuit breaker closed: operations restored");
                        }
                        break;
                        
                    default:
                        // No action for other transitions
                        break;
                }
            });
        
        kmsCircuitBreaker.getEventPublisher()
            .onError(event -> {
                // Our unique error tracking
                recordKmsFailure();
            });
        
        kmsCircuitBreaker.getEventPublisher()
            .onSuccess(event -> {
                // Our unique success tracking
                recordKmsSuccess();
            });
    }
    
    /**
     * Our business metric: Get circuit breaker state.
     */
    public String getCircuitBreakerState() {
        return kmsCircuitBreaker.getState().name();
    }
    
    /**
     * Our business metric: Get circuit breaker metrics.
     */
    public CircuitBreaker.Metrics getCircuitBreakerMetrics() {
        return kmsCircuitBreaker.getMetrics();
    }
}
