package com.example.stock.trasaction;

import com.example.stock.service.StockService;
import com.sun.source.doctree.EndElementTree;
import jdk.swing.interop.SwingInterOpUtils;

public class TransactionStockService {

    private StockService stockService;

    public TransactionStockService(StockService stockService) {
        this.stockService = stockService;
    } //생성자 주입

    public void decrease(Long id, Long quantity){
        startTransaction();
        //Transaction을 시작하게 된다면

        stockService.decrease(id, quantity);
        //메서드 호출 하고

        endTransaction();
        //메서드 호출이 종료 되면 Transaction 종료 <- 여기서 문제 발생
        //Transaction 종료 시점에 DB업데이트를 하는데 decrease 메서드 완료 후 db업데이트가 되기전에 다른스레드가
        //decrease 메서드를 호출 할수 있다. <- 다른 스레드는 갱신되기전에 값을 가져가서 문제 발생
    }

    private void endTransaction() {
        System.out.println("Commit");
    }

    private void startTransaction() {
        System.out.println("Transaction start");
    }


}
