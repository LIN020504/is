package com.example.web.tools;

import org.springframework.stereotype.Component;

@Component
public class CacheStatSwitch {
    private volatile boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

