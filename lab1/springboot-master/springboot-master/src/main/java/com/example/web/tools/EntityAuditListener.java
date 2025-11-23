package com.example.web.tools;

import com.example.web.entity.BaseEntity;
import com.example.web.tools.dto.CurrentUserDto;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * EclipseLink 实体监听器
 * 用于在实体持久化或更新前自动填充公共字段
 */
@Component
public class EntityAuditListener {

    /**
     * 插入前执行（相当于 MyBatis-Plus 的 insertFill）
     */
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {

            // 创建时间为空时自动赋值
            if (baseEntity.getCreationTime() == null) {
                baseEntity.setCreationTime(LocalDateTime.now());
            }

            // 获取当前用户上下文
            CurrentUserDto currentUser = BaseContext.getCurrentUserDto();

            // 如果有登录用户，则设置创建人 ID
            if (currentUser != null && baseEntity.getCreatorId() == null) {
                baseEntity.setCreatorId(Long.valueOf(currentUser.getUserId()));
            }
        }
    }

    /**
     * 更新前执行（目前无操作，可扩展）
     */
    @PreUpdate
    public void preUpdate(Object entity) {
        // 例如，可以在此添加 lastModifiedTime 自动更新时间等逻辑
    }
}
