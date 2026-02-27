package com.github.messenger.infrastructure.websocket;

import com.github.messenger.infrastructure.websocket.validators.ValidateError;
import com.github.messenger.infrastructure.websocket.validators.Validator;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RequestValidator {
    private HashMap<Integer, Validator> validators = new HashMap<>();

    public void registerValidator(int opcode, Validator validator) {
        validators.put(opcode, validator);
    }

    public void validate(int opcode, String message) {
        Validator validator = validators.get(opcode);
        if (validator != null) {
            validator.validate(opcode, message);
        }
        else {
            throw new ValidateError("Не верный opcode");
        }
    }
}
