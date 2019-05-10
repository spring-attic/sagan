package sagan.support.cache;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

public class JsonRedisTemplate<V> extends RedisTemplate<String, V> {

    public JsonRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper, Class valueType) {
		JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        setKeySerializer(jdkSerializer);
        setHashKeySerializer(jdkSerializer);
        setHashValueSerializer(jdkSerializer);
        Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(valueType);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        setValueSerializer(jsonRedisSerializer);
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }

}
