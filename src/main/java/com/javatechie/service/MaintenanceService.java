package com.javatechie.service;

public interface MaintenanceService {
    void enableMaintenanceMode();

    void disableMaintenanceMode();

    boolean isMaintenanceModeEnabled();
}
