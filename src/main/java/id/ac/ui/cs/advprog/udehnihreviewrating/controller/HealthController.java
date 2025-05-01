package id.ac.ui.cs.advprog.udehnihreviewrating.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/")
    public String root() {
        return "Welcome";
    }

    @GetMapping("/api/secure")
    public String secure() {
        return "Secret";
    }
}
