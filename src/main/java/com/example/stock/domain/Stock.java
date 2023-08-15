package com.example.stock.domain;

import javax.persistence.*;

@Entity
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long produceId;

    private Long quqntity;

//    @Version
//    private Long version; //optimistic lock을 사용하기 위한 버전 컬럼

    public Stock(){

    }

    public Stock(Long produceId, Long quqntity) {
        this.produceId = produceId;
        this.quqntity = quqntity;
    }

    public Long getQuqntity() {
        return quqntity;
    }

    public void decrease(Long quantity){
        if(this.quqntity - quantity < 0){ //현재 수량 - 감소시킬 수량 < 0
            throw new RuntimeException("재고는 0개 미만이 될수 없습니다.");
        }

        this.quqntity -= quantity; //0개미만이 아니라면 갱신
    }
}
