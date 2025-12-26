package com.example.web.service;

import com.example.web.tools.WebSocketHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifyService {

    @Autowired
    private WebSocketHandlerImpl webSocketHandler;

    public void notifyUpdate(String msg) {
        webSocketHandler.sendToAll(msg);
    }
}

