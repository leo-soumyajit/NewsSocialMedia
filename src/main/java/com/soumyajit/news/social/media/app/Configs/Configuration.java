package com.soumyajit.news.social.media.app.Configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
