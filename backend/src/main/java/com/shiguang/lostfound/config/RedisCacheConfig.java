package com.shiguang.lostfound.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                warn("读取", exception, cache, key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                warn("写入", exception, cache, key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                warn("删除", exception, cache, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                warn("清空", exception, cache, "*");
            }

            private void warn(String operation, RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis缓存{}失败，已回退数据库 cache={} key={} reason={}",
                        operation, cache.getName(), key, exception.getMessage());
            }
        };
    }
}
