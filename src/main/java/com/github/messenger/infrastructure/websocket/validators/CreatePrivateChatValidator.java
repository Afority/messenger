package com.github.messenger.infrastructure.websocket.validators;

import com.github.messenger.infrastructure.websocket.RequestValidator;
import com.github.messenger.infrastructure.websocket.request_types.CreatePrivateChatRequest;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CreatePrivateChatValidator implements Validator {

    public CreatePrivateChatValidator(RequestValidator requestValidator) {
        requestValidator.registerValidator(50, this);
    }

    @Override
    public void validate(int opcode, String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CreatePrivateChatRequest request = objectMapper.readValue(jsonStr, CreatePrivateChatRequest.class);
            if (request.userId() > 0){
                return; // success
            }
            else{
                throw new ValidateError("Id пользователя не должно быть отрицательно");
            }
        } catch (JacksonException e) {
            System.out.println("ERROR: " + e.getMessage());
            throw new ValidateError("Ошибка структуры запроса");
        }
        // throw new ValidateError("Internal server error");
    }
}
