package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonLockStockFacadeTest {

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before(){
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll(); //테스트 종료시정보 삭제
    }

    @Test
    public void 동시에_100개_요청() throws InterruptedException {
        int threadCount = 100; //동시에 여러 요청을 보내야 하기 때문에 멀티 스레드 사용

        ExecutorService executorService = Executors.newFixedThreadPool(32); //
        //멀티스레드 사용 , 비동기로 실행하는 작업을 단순하게 사용할수 있는 api

        CountDownLatch latch = new CountDownLatch(threadCount);//100개 요청 끝날떄까지 기다려야 하므로
        ////다른 스레드에서 수행되는 작업이 완료될때까지 대기할수있도록 하는 클래스

        for(int i = 0; i<threadCount; i++){
            executorService.submit(() ->{
                try{
                    redissonLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown(); //쓰레드 끝날떄마다 카운트를 감소
                }
            }); //100개의 요청
        }
        latch.await(); //카운트가 0이 되면 대기가 풀리고 이후 쓰레드가 실행

        Stock stock = stockRepository.findById(1L).orElseThrow();
        //100 - (1*100) = 0 1개씩 100번 감소기떄문에 0개 이상예상이 될거싱다

        Assertions.assertThat(stock.getQuqntity()).isEqualTo(0L);
    }

}