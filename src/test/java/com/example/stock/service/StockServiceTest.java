package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.PessimisticLockStockService;
import com.example.stock.service.StockService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class StockServiceTest {

//    @Autowired
//    private StockService stockService;
//

    @Autowired
    private PessimisticLockStockService stockService;
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
    public void 재고감소(){
        stockService.decrease(1L, 1L);

        //100 - 1 99개

        Stock stock = stockRepository.findById(1L).orElseThrow();

        Assertions.assertThat(stock.getQuqntity()).isEqualTo(99L);
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
                    stockService.decrease(1L, 1L);
                }finally {
                    latch.countDown(); //쓰레드 끝날떄마다 카운트를 감소
                }
            }); //100개의 요청
        }
        latch.await(); //카운트가 0이 되면 대기가 풀리고 이후 쓰레드가 실행

        Stock stock = stockRepository.findById(1L).orElseThrow();
        //100 - (1*100) = 0 1개씩 100번 감소기떄문에 0개 이상예상이 될거싱다

        Assertions.assertThat(stock.getQuqntity()).isEqualTo(0L);

        //레이스 컨디션이 일어나서 에러 //레이스 컨디션이란 두 이상의 스레드가 공유 데이터에 엑세스 할수 있고, 동시에 변경을 하려 할떄 발생하는 문제
        //96L
        //이런 문제 해결방법은 하나의 스레드가 작업이 완료한 이후에 다른 스레드가 접근이 가능하도록 해야함
        //1. decrease 메서드 선언부 앞에 synchronized 붙여주면 한개의 쓰레드만 접근 가능 -> but 50개 남음
        //스프링의 @Transactonal 동작 방식 때문에 우리가 만든 클래스를 새로 만들어서 실행 -> @Transactional 어노테이션 주석으로 해결
        /*synchronized의 문제점 : 각 하나의 프로세스 안에서만 보장이 됨. 서버가 한대일땐 db접근을 서버 한대만 해서 괜찮지만
        서버 두개이상부터 문제가 있을수 있다.
        */
        //여러 쓰레드에서 동시에 접근이 가능하게 되면서 레이스 컨디션 발생

    }
}
