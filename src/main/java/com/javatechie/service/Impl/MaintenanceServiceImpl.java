package com.javatechie.service.Impl;

import com.javatechie.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final Jedis jedis;
    private static final String MAINTENANCE_MODE_KEY = "maintenance_mode";


    @Override
    public void enableMaintenanceMode() {
        jedis.set(MAINTENANCE_MODE_KEY, "true");
    }

    @Override
    public void disableMaintenanceMode() {
        jedis.del(MAINTENANCE_MODE_KEY);
    }

    @Override
    public boolean isMaintenanceModeEnabled() {
        return jedis.exists(MAINTENANCE_MODE_KEY);
    }
}
