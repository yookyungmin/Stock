package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//편의성을 위해 stock엔티티를 사용하는데 실무에선 별로의 jdbc사용해야함
//named lock
public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);

}

//명령어 모두 작성하였따면 실제 로직 전후 Lock 획득 해제를 해주어야 하기 떄문에 facade클래스 추가해야함
/*
특히 named lock 은 데이터소스를 분리해야 한다고 강조했던 이유는 커넥션을 2개를 사용하기 때문입니다.
lock 획득에 필요한 connection 1개, transaction (로직) 에 필요한 커넥션 1개


 */