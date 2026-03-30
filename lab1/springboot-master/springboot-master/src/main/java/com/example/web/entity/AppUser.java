package com.example.web.entity;

import com.example.web.dto.AppUserDto;
import com.example.web.tools.EntityAuditListener;
import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(EntityAuditListener.class)
@Table(name = "appuser")
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public class AppUser extends BaseEntity {

    /** 账号 */
    @Column(name = "UserName", nullable = false, length = 50)
    private String userName;

    /** 密码 */
    @Column(name = "Password", nullable = false, length = 100)
    private String password;

    /** 邮箱 */
    @Column(name = "Email", length = 100)
    private String email;

    /** 名称 */
    @Column(name = "Name", length = 50)
    private String name;

    /** 手机号码 */
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;

    /** 出生年月 */
    @Column(name = "Birth")
    private LocalDateTime birth;

    /** 用户角色 */
    @Column(name = "RoleType")
    private Integer roleType;

    /** 眼睛颜色 */
    @Column(name = "EyeColor")
    private String eyeColor = "Unknown";

    /** 头发颜色 */
    @Column(name = "HairColor")
    private String hairColor = "Unknown";

    /** 身高 */
    @Column(name = "Height")
    private Double height;

    /** 国籍 */
    @Column(name = "Nationality")
    private String nationality;

    /** 地点 */
    @Column(name = "Location")
    private String location;

    /** 转换为 DTO */
    public AppUserDto MapToDto() throws InvocationTargetException, IllegalAccessException {
        AppUserDto dto = new AppUserDto();
        BeanUtils.copyProperties(dto, this);
        return dto;
    }
}
