package com.mindmirror;

import com.mindmirror.config.MindMirrorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MindMirrorProperties.class)
public class MindMirrorApplication {
    public static void main(String[] args) {
        SpringApplication.run(MindMirrorApplication.class, args);
    }
}
