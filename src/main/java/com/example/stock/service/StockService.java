package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    //@Transactional //synchronize을 쓸떈 주석처리
//    public synchronized void decrease(Long id, Long quantity){
//        //stock 조회
//        //재고를 감소한뒤
//        //갱신한값을 저장
//
//        Stock stock = stockRepository.findById(id).orElseThrow();
//
//        stock.decrease(quantity);
//
//        stockRepository.saveAndFlush(stock);
//    }


    @Transactional(propagation = Propagation.REQUIRES_NEW) //부모의 Transaction 과 별도의 실행이 되어야 하기 떄문에 Propagation 변경
    public void decrease(Long id, Long quantity){
        //stock 조회
        //재고를 감소한뒤
        //갱신한값을 저장

        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}

