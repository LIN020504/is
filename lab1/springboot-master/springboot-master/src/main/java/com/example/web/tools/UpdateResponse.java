package com.example.web.tools;

// 响应结构：通知客户端是否有更新
public class UpdateResponse {
    private boolean hasUpdates;

    public UpdateResponse(boolean hasUpdates) {
        this.hasUpdates = hasUpdates;
    }

    public boolean isHasUpdates() {
        return hasUpdates;
    }
    // 需要 getter/setter 以便 Spring 序列化
}
