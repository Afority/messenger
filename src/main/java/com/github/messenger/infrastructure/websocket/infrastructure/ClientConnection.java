package com.github.messenger.infrastructure.websocket.infrastructure;

import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection {
    private final WebSocketSession session;
    private final BlockingQueue<String> messages;
    private final ExecutorService executor;
    private volatile boolean isRunning = false;

    public String getId() {
        return session.getId();
    }

    public void close() {
        stopSendingMessages();
        try {
            session.close();
        }
        catch (IOException ignored) {}
    }

    public ClientConnection(WebSocketSession session) {
        this.session = session;
        this.messages = new LinkedBlockingQueue<>();
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void stopSendingMessages() {
        isRunning = false;
    }

    public void registerMessage(String message) {
        messages.add(message);
    }

    public void runSendMessageLoop() {
        isRunning = true;
        executor.submit(() -> {
            while (isRunning) {
                try {
                    if (messages.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }
                    if (messages.size() > 100) {
                        session.close();
                        return;
                    }
                    String message = messages.poll();
                    session.sendMessage(new TextMessage(message));
                } catch (IOException | InterruptedException ignored) {}
            }
        });
    }
}
