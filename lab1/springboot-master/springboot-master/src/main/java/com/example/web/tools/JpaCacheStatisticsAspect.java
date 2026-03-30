package com.example.web.tools;

import jakarta.persistence.EntityManagerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JpaCacheStatisticsAspect {

    private final EntityManagerFactory emf;
    private final CacheStatSwitch statSwitch;

    public JpaCacheStatisticsAspect(EntityManagerFactory emf, CacheStatSwitch statSwitch) {
        this.emf = emf;
        this.statSwitch = statSwitch;
    }

    @Around("@within(org.springframework.stereotype.Repository)")
    public Object logCacheStats(ProceedingJoinPoint pjp) throws Throwable {
        if (!statSwitch.isEnabled()) {
            return pjp.proceed();
        }

        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        long hitsBefore = stats.getSecondLevelCacheHitCount();
        long missesBefore = stats.getSecondLevelCacheMissCount();
        long putsBefore = stats.getSecondLevelCachePutCount();

        Object result = pjp.proceed();

        long hitsAfter = stats.getSecondLevelCacheHitCount();
        long missesAfter = stats.getSecondLevelCacheMissCount();
        long putsAfter = stats.getSecondLevelCachePutCount();

        long hitDelta = hitsAfter - hitsBefore;
        long missDelta = missesAfter - missesBefore;
        long putDelta = putsAfter - putsBefore;

        if (hitDelta + missDelta + putDelta > 0) {
            System.out.printf("[JPA L2 Cache] Hits: %d, Misses: %d, Puts: %d, Method: %s%n",
                    hitDelta, missDelta, putDelta, pjp.getSignature().toShortString());
        }

        return result;
    }
}
