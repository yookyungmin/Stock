package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;

    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while(!redisLockRepository.lock(id)){ //스핀락(락을 획득할때까지 반복해서 체크) 방식으로 락획득 재시도
            Thread.sleep(100);
        }
        try{
            stockService.decrease(id, quantity);
        }finally {
            redisLockRepository.unLock(id);
        }
    }
}

/*
1. while문 활용해서 lock 획득 시도 lock 획득에 실패 하였다면 100밀리 세컨즈 텀을 두고 lock 재시도, redis 부하 줄이기
2. lock 획득에 성공하였다면서 stockservice활용해서 재고 감소, 로직 종료시 unlock메서드로 lock 해제
 */