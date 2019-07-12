

package com.controllers;

import org.neo4j.driver.v1.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    protected static Session session;

    public static void main(String[] args) {
        try {
            Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","111096"));
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