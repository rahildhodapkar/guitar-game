package com.rahildhodapkar.guitar_game;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PingController {
    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        Thread t = Thread.currentThread();
        log.info("Handling /api/ping on thread={}, virtual={}", t.getName(), t.isVirtual());
        return Map.of(
                "thread", t.getName(),
                "virtual", t.isVirtual(),
                "threadClass", t.getClass().getSimpleName());
    }

}
