package com.example.web.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

/**
 * 所有实体类的通用父类
 * 使用 JPA（EclipseLink）实现 ORM
 */
@Data
@MappedSuperclass // ✅ 表示这是一个被继承的父类，不单独映射为表
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public abstract class BaseEntity {

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Version
    private Integer version;

    /** 创建时间 */
    @Column(name = "CreationTime", nullable = false)
    private LocalDateTime creationTime = LocalDateTime.now();

    /** 创建人 ID */
    @Column(name = "CreatorId")
    private Long creatorId;
}
