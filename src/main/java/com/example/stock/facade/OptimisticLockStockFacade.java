package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//OptimisticLock 실패했을때 재시도를 해야 하므로
@Component
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while(true){
            try{
                optimisticLockStockService.decrease(id, quantity);
                break; //정상 적으로 업데이트 되면 while문 빠져나가기
            }catch (Exception e){
                Thread.sleep(50); //수량 감소에 실패하게 된다면 0.05초 있다 재시도
            }
        }
    }
}
