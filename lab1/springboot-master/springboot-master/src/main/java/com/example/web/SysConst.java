package com.example.web;

public class SysConst {

    //业务成功
    public final static String STATUS_200 = "200";

    //业务失败
    public final static String STATUS_500 = "500";

    //没有认证授权
    public final static String STATUS_401 = "401";

    public static final  String UserIdClaim="UserId";

    public static final  String RoleTypeClaim="RoleType";

    // HTTP Header
    public static final String Authorization = "Authorization";

    // Request 中保存 JWT Claims
    public static final String JwtClaims = "JWT_CLAIMS";

    // 系统管理员角色值（按实际角色调整）
    public static final int AdminRole = 1;
}
