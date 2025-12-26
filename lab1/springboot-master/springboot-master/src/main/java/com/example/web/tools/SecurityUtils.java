package com.example.web.tools;

import com.example.web.SysConst;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class SecurityUtils {

    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getClaims() {
        return (Map<String, String>)
                getRequest().getAttribute(SysConst.JwtClaims);
    }

    public static Long getUserId() {
        Map<String, String> claims = getClaims();
        if (claims == null) return null;
        return Long.valueOf(claims.get(SysConst.UserIdClaim));
    }

    public static Integer getRoleType() {
        Map<String, String> claims = getClaims();
        if (claims == null) return null;
        return Integer.valueOf(claims.get(SysConst.RoleTypeClaim));
    }

    public static boolean isAdmin() {
        Integer roleType = getRoleType();
        return roleType != null && roleType == SysConst.AdminRole;
    }
}
