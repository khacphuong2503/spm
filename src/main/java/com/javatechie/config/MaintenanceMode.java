package com.javatechie.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceMode {

    private final RedisTemplate<String, Boolean> redisTemplate;
    private final String maintenanceModeKey = "maintenanceMode";

    public MaintenanceMode(RedisTemplate<String, Boolean> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isEnabled() {
        Boolean value = redisTemplate.opsForValue().get(maintenanceModeKey);
        return value != null && value;
    }

    public void setEnabled(boolean enabled) {
        redisTemplate.opsForValue().set(maintenanceModeKey, enabled);
    }
}
