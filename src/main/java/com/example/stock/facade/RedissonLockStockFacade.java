package com.example.stock.facade;


import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {

    private RedissonClient redissonClient;

    private StockService stockService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity){
        RLock lock = redissonClient.getLock(id.toString()); //lock 객체 가져오기

        try{
            boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS); //몇초동안 lock 획득 시도할것인지, 점유할것인지 설정
            //락 획득 재시도

            if(!available){
                System.out.println("lock 획득 실패");
                return;
            }

            stockService.decrease(id, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally { //로직 정상 종료 시
            lock.unlock();
        }

    }
}
