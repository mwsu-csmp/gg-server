

package com.controllers;

import org.neo4j.driver.v1.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:game-layout.xml")
public class Application {
    protected static Session session;

    public static void main(String[] args) {
        try {
            Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j",""));
            session = driver.session();
        } catch(Error e){
            e.printStackTrace();
        }
        SpringApplication.run(Application.class, args);
    }

    public static Application loadMap(String s) {
        return null;
    }
}