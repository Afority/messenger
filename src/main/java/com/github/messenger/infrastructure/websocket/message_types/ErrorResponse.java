package com.github.messenger.infrastructure.websocket.message_types;

/*
{
    "requestId": int,
    "type": "RESPONSE",
    "payload": {
        "success": true,
        "message": ""
    }
}
*/
public class ErrorResponse extends BasicResponse {
    public ErrorResponse(int requestId, String message) {
        super(requestId, new ErrorPayload(message));
    }
}
