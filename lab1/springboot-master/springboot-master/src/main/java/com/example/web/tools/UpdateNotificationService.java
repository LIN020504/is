package com.example.web.tools;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class UpdateNotificationService {

    // 存储挂起的请求，键是唯一ID，值是DeferredResult对象
    private final Map<String, DeferredResult<UpdateResponse>> deferredResults = new ConcurrentHashMap<>();

    // 超时时间：例如 30 秒 (单位毫秒)
    private static final long TIMEOUT = 30000L;

    /**
     * 1. 挂起请求
     * @param clientId 客户端唯一标识 (可以是 Session ID 或 Token)
     * @return 挂起的 DeferredResult
     */
    public DeferredResult<UpdateResponse> waitForUpdate(String clientId) {
        // 创建 DeferredResult，设置超时处理
        DeferredResult<UpdateResponse> deferredResult = new DeferredResult<>(TIMEOUT, new UpdateResponse(false));

        // 注册完成和超时回调
        deferredResult.onCompletion(() -> deferredResults.remove(clientId));
        deferredResult.onTimeout(() -> {
            // 超时时返回“无更新”
            deferredResult.setResult(new UpdateResponse(false));
            deferredResults.remove(clientId);
        });

        deferredResults.put(clientId, deferredResult);
        return deferredResult;
    }

    /**
     * 2. 数据更新时，通知所有挂起的请求
     * * @param updateType 发生的更新类型 (例如 USER_CRUD)
     */
    public void notifyClientsOfUpdate(String updateType) {
        // 遍历所有挂起的请求，并设置结果（触发响应）
        for (Map.Entry<String, DeferredResult<UpdateResponse>> entry : deferredResults.entrySet()) {
            DeferredResult<UpdateResponse> deferredResult = entry.getValue();
            // 设置结果为“有更新”，客户端收到后会刷新表格，并立即发起下一个请求
            if (!deferredResult.isSetOrExpired()) {
                deferredResult.setResult(new UpdateResponse(true));
            }
        }
    }
}

