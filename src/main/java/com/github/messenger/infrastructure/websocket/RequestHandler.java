package com.github.messenger.infrastructure.websocket;

import com.github.messenger.infrastructure.websocket.validators.ValidateError;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RequestHandler {
    private final RequestValidator requestValidator;
    private final ObjectMapper objectMapper;

    public RequestHandler(RequestValidator requestValidator) {
        this.requestValidator = requestValidator;
        this.objectMapper = new ObjectMapper();
    }

    private record BasicRequest(
            Integer opcode
    ){}

    public void handle(String message, WebSocketSession session) {
        int opcode = getOpcode(message);
        if (opcode == -1){
            try {
                session.sendMessage(new TextMessage("{\"error\":true, \"message\":\"opcode not found\""));
            } catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
                return;
            }
        }

        try {
            requestValidator.validate(opcode, message);
            session.sendMessage(new TextMessage("{\"message\":\"success\"}"));
        }
        catch (ValidateError e) {
            try {
                session.sendMessage(new TextMessage(String.format("{\"error\":true, \"message\":{\"%s\"}}", e.getMessage())));
            } catch (IOException ex) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println("ERROR: " + e.getMessage());
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private int getOpcode(String message) {
        try{
            return objectMapper.readValue(message, BasicRequest.class).opcode;
        }
        catch (Exception e){
            return -1;
        }
    }
}
