package com.stoloto.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SseEventService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(60_000L); // 60 seconds timeout
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        sendEvent(emitter, "connected", "You are now connected to SSE endpoint");
        return emitter;
    }

    public void broadcastEvent(String eventName, Object data) {
        executor.execute(() -> {
            for (SseEmitter emitter : emitters) {
                sendEvent(emitter, eventName, data);
            }
        });
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}