package com.github.messenger.infrastructure.websocket.validators;

public interface Validator {
    /**
     * @throws ValidateError -
     */
    void validate(int opcode, String jsonStr);
}
