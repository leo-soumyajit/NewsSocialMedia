package com.soumyajit.news.social.media.app.controller;

import com.soumyajit.news.social.media.app.Advices.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthCheckController {

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData("Ok");
        return ResponseEntity.ok().body(response);
    }

}
