package com.example.stock.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLockRepository {
    private RedisTemplate<String, String> redisTemplate;

    public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    } //redis 명령어 실행 위한 템플릿 di

    public boolean lock(Long key){
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    } //key 에 사용할 변수를 받아야 하기 때문에 key 매개변수 추가

    public boolean unLock(Long key){
        return redisTemplate.delete(generateKey(key));
    }
    //로직 실행전에 key 와 setnx 명령어를 활용해서 lock을 하고 unlock 메시지를 통해 lock을 해제하는 방식
    private String generateKey(Long key){
        return key.toString();
    }
}
//1. redis 명령어를 이용할수 있어야 하기 때문에 레디스 레파지토리 생성
