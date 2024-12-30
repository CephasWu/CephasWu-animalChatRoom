package com.example.test_redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    /**
     * 主要的redis連線
     */
    @Bean
    @Primary
    public StringRedisTemplate first(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    /**
     * 次要的redis連線，另外設定connectionFactory以連線到另一個redis
     *
     * @param database  Redis資料庫索引
     * @param timeout   連線超時時間（毫秒）
     * @param maxActive 連線池最大連線數（使用負值表示沒有限制）
     * @param maxWait   連線池最大等待時間（使用負值表示沒有限制）
     * @param maxIdle   連線池中的最大空閒連線
     * @param minIdle   連線池中的最小空閒連線
     * @param host      Redis伺服器地址
     * @param port      Redis伺服器連線埠
     */
    @Bean
    public StringRedisTemplate second(
            @Value("${spring.redis.database}")
                    int database,
            @Value("${spring.redis.timeout}")
                    long timeout,
            @Value("${spring.redis.lettuce.pool.max-active}")
                    int maxActive,
            @Value("${spring.redis.lettuce.pool.max-wait}")
                    int maxWait,
            @Value("${spring.redis.lettuce.pool.max-idle}")
                    int maxIdle,
            @Value("${spring.redis.lettuce.pool.min-idle}")
                    int minIdle,
            @Value("${spring.redis.host}")
                    String host,
            @Value("${spring.redis.port}")
                    int port
    ) {
        // connection config
        var configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(database);

        // pool config
        var genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(maxWait));

        // create connection factory
        var builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(genericObjectPoolConfig);
        builder.commandTimeout(Duration.ofSeconds(timeout));
        var connectionFactory = new LettuceConnectionFactory(
                configuration, builder.build()
        );
        connectionFactory.afterPropertiesSet();

        // create redis template
        return createRedisTemplate(connectionFactory);

    }

    /**
     * 建立StringRedisTemplate
     * 此function不能加 @Bean 否則connectionFactory將會一律採用預設值
     */
    private StringRedisTemplate createRedisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
