package com.controllers;

import edu.missouriwestern.csmp.gg.base.DataStore;
import edu.missouriwestern.csmp.gg.base.HasProperties;
import edu.missouriwestern.csmp.gg.base.Player;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

class Neo4jDatastore implements DataStore {

    private Session session;
    private static Logger logger = Logger.getLogger(GameMapping.class.getCanonicalName());


    public Neo4jDatastore(String url, String user, String password) throws Exception{
        session = GraphDatabase.driver(url, AuthTokens.basic(user, password)).session();
    }

    private static String[] GetPropertyTable(HasProperties object) {
        String[] array = new String[2];
        String properties = "";

        for (Map.Entry properties1 : object.getProperties().entrySet()) {
            properties += String.format(" %s: ", properties1.getKey()) + properties1.getValue() + ",";
        }

        if (properties.endsWith(",")) {
            properties = properties.substring(1, properties.length() - 1);
        }

        String type = "";

        if (object instanceof PlayerAvatar) {
            type = "Player";
        } else {
            type = "Entity";
        }

        array[0] = type;
        array[1] = properties;
        return array;
    }

    @Override
    public void save(HasProperties hasProperties) {
        StatementResult statementResult = null;
        try {
            statementResult = session.run(
                    String.format("MATCH (n { id: \'%s\' })\nReturn n", hasProperties.getProperty("id"))
            );
        }catch(Error e){}
        if (statementResult != null) {
            var list = statementResult.list();
            if (!list.isEmpty()) {
                logger.info("Found result for id:" + hasProperties.getProperty("id"));
                String[] newTable = GetPropertyTable(hasProperties);
                session.run(String.format("MATCH (n { id: \'%s\' })\nSet n = { %s }", hasProperties.getProperty("id"), newTable[1]));
            }
        } else {
            logger.info("Couldn't find result for id:" + hasProperties.getProperty("id"));
            String[] newTable = GetPropertyTable(hasProperties);
            logger.info("" + newTable[0] + "\t" + newTable[1]);
            session.run(String.format("CREATE (n:%s { %s })", newTable[0], newTable[1]));
        }
    }

    @Override
    public void load(HasProperties hasProperties) {
        StatementResult statementResult = null;
        try {
            statementResult = session.run(
                    String.format("MATCH (n { id: \'%s\' })\nReturn n", hasProperties.getProperty("id"))
            );
        }catch(Error e){}
        if (statementResult != null){
            var list = statementResult.list();
            if (!list.isEmpty()) {
                logger.info("Found result for id:" + hasProperties.getProperty("id"));
                Node n = list.get(0).get(0).asNode();
                for (String s : n.keys()) {
                    hasProperties.setProperty(s, String.valueOf(n.get(s)));
                }
            }
        } else {
            save(hasProperties);
        }
    }

    @Override
    public ArrayList<Integer> search(Map map){
        ArrayList<Integer> list = new ArrayList<>();
        StatementResult statementResult = null;
        try {
            statementResult = session.run(
                    String.format("MATCH (n { %s })\nReturn n", map.toString())
            );
        }catch(Error e){}
        if (statementResult != null){
            var list1 = statementResult.list();
            if (!list1.isEmpty()){
                for (Record r: statementResult.list()){
                    var id = r.get(0).asNode().get("id");
                    list.add(id.asInt());
                }
            }
        }
        return list;
    }

    @Override
    public int getMaxEntityId() {
        var returnedValue = -1;
        StatementResult statementResult = null;
        try {
            statementResult = session.run(
                    String.format("MATCH (n)\nReturn n")
            );
        }catch(Error e){}
        if (statementResult != null) {
            var list1 = statementResult.list();
            if (!list1.isEmpty()) {
                for (Record r : statementResult.list()) {
                    var id = r.get(0).asNode().get("id");
                    returnedValue = Math.max(returnedValue, id.asInt());
                }
            }
        }
        return returnedValue;
    }
}
