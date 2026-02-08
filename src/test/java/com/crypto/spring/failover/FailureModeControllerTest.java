package com.crypto.spring.failover;

import com.crypto.spring.failover.FailureModeController;
import com.crypto.spring.failover.FailureModeTransitionEvent;
import com.crypto.spring.model.FailureMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for FailureModeController to verify our unique state transition logic.
 */
class FailureModeControllerTest {
    
    private FailureModeController controller;
    private ApplicationEventPublisher eventPublisher;
    
    @BeforeEach
    void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        controller = new FailureModeController(eventPublisher);
    }
    
    @Test
    void shouldStartInNormalMode() {
        // Then: Initial mode should be NORMAL
        assertEquals(FailureMode.NORMAL, controller.getCurrentMode());
        assertTrue(controller.canPerformOperations());
    }
    
    @Test
    void shouldTransitionToDegraded() {
        // When: We transition to degraded mode
        controller.transitionToMode(FailureMode.DEGRADED, "Test degradation");
        
        // Then: Mode should change and event should be published
        assertEquals(FailureMode.DEGRADED, controller.getCurrentMode());
        verify(eventPublisher, times(1)).publishEvent(any(FailureModeTransitionEvent.class));
    }
    
    @Test
    void shouldTransitionToLocked() {
        // When: We lock operations
        controller.lockOperations("Security incident");
        
        // Then: Mode should be locked and operations not allowed
        assertEquals(FailureMode.LOCKED, controller.getCurrentMode());
        assertFalse(controller.canPerformOperations());
    }
    
    @Test
    void shouldRecoverFromDegraded() {
        // Given: System is in degraded mode
        controller.transitionToMode(FailureMode.DEGRADED, "KMS failure");
        
        // When: We recover
        controller.unlockOperations("KMS restored");
        
        // Then: Mode should be normal
        assertEquals(FailureMode.NORMAL, controller.getCurrentMode());
    }
    
    @Test
    void shouldNotPublishEventForSameMode() {
        // When: We transition to the same mode
        reset(eventPublisher);
        controller.transitionToMode(FailureMode.NORMAL, "Already normal");
        
        // Then: No event should be published
        verify(eventPublisher, never()).publishEvent(any());
    }
    
    @Test
    void shouldProvideCircuitBreakerState() {
        // When: We get circuit breaker state
        String state = controller.getCircuitBreakerState();
        
        // Then: It should be a valid state
        assertNotNull(state);
        assertTrue(state.equals("CLOSED") || state.equals("OPEN") || state.equals("HALF_OPEN"));
    }
}
