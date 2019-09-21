package edu.missouriwestern.csmp.gg.server.networking;

import java.util.Arrays;

public class Message {
    private String content;
    private String[] arraycontent;

    public Message(){}

    public Message(String content){
        this.content=content;
    }
    public Message(String[] content){
        this.arraycontent= Arrays.copyOf(arraycontent, arraycontent.length);
    }
    public String getContent(){
        return content;
    }
}