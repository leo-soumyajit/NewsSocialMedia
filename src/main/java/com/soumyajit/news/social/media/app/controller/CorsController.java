package com.soumyajit.news.social.media.app.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @CrossOrigin(origins = "http://localhost:63342")
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public void handlePreflight() {
        // This method is intentionally left empty to handle preflight requests
    }
}
