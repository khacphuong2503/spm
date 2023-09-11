package com.javatechie.controller.admin;

import com.javatechie.config.MaintenanceMode;
import com.javatechie.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceMode maintenanceMode;

    private final MaintenanceService mantenanceService;

    @GetMapping("/some-endpoint")
    public String handleRequest() {
        if (mantenanceService.isMaintenanceModeEnabled()) {
            // Logic xử lý khi trong trạng thái maintenance mode
            return "Ứng dụng đang trong trạng thái bảo trì.";
        } else {
            // Logic xử lý khi không trong trạng thái maintenance mode
            return "Xử lý yêu cầu bình thường.";
        }
    }

    @PostMapping("/maintenance/enable")
    public void enableMaintenanceMode() {
        mantenanceService.enableMaintenanceMode();
    }

    @PostMapping("/maintenance/disable")
    public void disableMaintenanceMode() {
         mantenanceService.disableMaintenanceMode();
    }

    @GetMapping("/maintenance/status")
    public boolean isMaintenanceModeEnabled() {
        return mantenanceService.isMaintenanceModeEnabled();
    }
}
