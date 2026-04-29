package com.github.messenger.infrastructure.websocket.infrastructure;

import com.github.messenger.domain.exceptions.DomainException;
import com.github.messenger.domain.value_objects.UserId;
import com.github.messenger.infrastructure.websocket.infrastructure.exceptions.RequestValidationException;
import com.github.messenger.infrastructure.websocket.message_types.BasicRequest;
import com.github.messenger.infrastructure.websocket.message_types.BasicResponse;
import com.github.messenger.infrastructure.websocket.message_types.ErrorResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequestHandler {
    private final ObjectMapper objectMapper;
    private final Map<Integer, OpcodeHandler> opcodeHandlers;

    public RequestHandler(List<OpcodeHandler> handlers) {
        this.objectMapper = new ObjectMapper();
        this.opcodeHandlers = new HashMap<>();

        handlers.forEach(handler -> opcodeHandlers.put(handler.getOpcode(), handler));
    }

    public void handle(String messageStr,
                       UserId userId,
                       UserCommunication clientSessions,
                       String requestedClientSessionId) {
        BasicRequest message = getBasicMessage(messageStr);
        if (message == null) {
            clientSessions.enqueueForUserSessionsExcept(
                    requestedClientSessionId,
                    new ErrorResponse(0, "opcode или request id не найден")
            );
            return;
        }

        OpcodeHandler handler = opcodeHandlers.get(message.getOpcode());
        if (handler == null) {
            clientSessions.enqueueForUserSessionsExcept(
                    requestedClientSessionId,
                    new ErrorResponse(0, "Код операции не найден")
            );
            return;
        }

        try {
            Object responseDto = handler.handleOpcode(message.getPayload(), userId);

            if (responseDto != null) {
                BasicResponse response = new BasicResponse(message.getRequestId(), responseDto);

                clientSessions.registerMessageForSession(
                        requestedClientSessionId,
                        objectMapper.writeValueAsString(response)
                );
            }
        }
        catch (DomainException e) {
            clientSessions.registerMessageForSession(
                    requestedClientSessionId,
                    new ErrorResponse(message.getRequestId(), e.getMessage())
            );
        }
        catch (RequestValidationException e) {
            clientSessions.registerMessageForSession(
                    requestedClientSessionId,
                    new ErrorResponse(message.getRequestId(), "Ошибка валидации запроса")
            );
        }
        catch (Exception e) {
            e.printStackTrace();
            clientSessions.registerMessageForSession(
                    requestedClientSessionId,
                    new ErrorResponse(message.getRequestId(), "Внутренняя ошибка сервера")
            );
        }
    }

    private BasicRequest getBasicMessage(String messageStr) {
        try{
            return objectMapper.readValue(messageStr, BasicRequest.class);
        }
        catch (Exception e){
            return null;
        }
    }
}
