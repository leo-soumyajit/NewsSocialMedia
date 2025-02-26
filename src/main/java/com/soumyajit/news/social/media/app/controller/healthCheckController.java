package com.soumyajit.news.social.media.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthCheckController {

    @GetMapping("/")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("OK");
    }

}
