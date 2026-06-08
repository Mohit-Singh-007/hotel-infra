package com.project.booking.idempotency;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.booking.exceptions.custom.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/*
* attach unique idempotency key to each req -> store in redis
* */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX = "idempotency:";
    private static final String PROCESSING = "PROCESSING";
    private static final long TTL_HOURS = 24;



    public <T> Optional<T> getResult(String key , Class<T> type){
        String redisKey = PREFIX+key;
        String cached = redisTemplate.opsForValue().get(redisKey);

        if(cached == null) return Optional.empty();
        if(cached.equals(PROCESSING)){
            throw new ConflictException("Request with this idempotency key is already being processed");
        }

        try{
            log.info("Idempotency key hit: {}",key);
            return Optional.of(objectMapper.readValue(cached,type));
        }catch (Exception e){
            log.warn("Failed to deserialize idempotency result for key {}", key);
            return Optional.empty();
        }
    }

    // atomic lock
    public boolean acquireLock(String key){
        String redisKey = PREFIX+key;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(redisKey,PROCESSING,30,TimeUnit.SECONDS);

        return Boolean.TRUE.equals(locked);
    }
    public void deleteLock(String key){
        redisTemplate.delete(PREFIX+key);
    }

    public <T> void saveResult(String key, T result){
        try {
            String redisKey = PREFIX+key;

            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(result),
                    TTL_HOURS,
                    TimeUnit.HOURS
            );
            log.info("Idempotency result saved for key {}", key);
        }catch (Exception e){
            log.warn("Failed to save idempotency result for key {}", e.getMessage());
        }
    }


}
