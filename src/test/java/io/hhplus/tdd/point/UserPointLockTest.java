package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserPointLockTest {
    private UserPointFacade userPointFacade;
    private UserPointTable userPointTable = new UserPointTable();
    private PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @BeforeEach
    void setUp() {
        UserPointService userPointService = new UserPointService(userPointTable, pointHistoryTable);
        PointHistoryService pointHistoryService = new PointHistoryService(pointHistoryTable);
        userPointFacade = new UserPointFacade(userPointService, pointHistoryService);
    }

    @Test
    @DisplayName("같은 유저 동시 포인트 충전 요청은 직렬 처리되어야 한다")
    void givenSameUser_whenConcurrentCharge_thenRequestsProcessedSerially() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);
        long id = 1L;

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    userPointFacade.chargePoint(id, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        UserPoint result = userPointTable.selectById(id);
        assertThat(result.point()).isEqualTo(100L);
    }

    @Test
    @DisplayName("다른 유저 동시 포인트 충전 요청은 병렬 처리되어야 한다")
    void givenDifferentUser_whenConcurrentCharge_thenRequestsProcessedParallel() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final long userId = i % 2 == 0 ? 10L : 20L;
            executor.submit(() -> {
                try {
                    userPointFacade.chargePoint(userId, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertThat(userPointTable.selectById(10L).point()).isEqualTo(50L);
        assertThat(userPointTable.selectById(20L).point()).isEqualTo(50L);
    }

    @Test
    @DisplayName("같은 유저 동시 포인트 사용 요청은 직렬 처리되어야 한다")
    void givenSameUser_whenConcurrentUse_thenRequestsProcessedSerially() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);
        long id = 1L;
        userPointFacade.chargePoint(id, 500);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    userPointFacade.usePoint(id, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        UserPoint result = userPointTable.selectById(id);
        assertThat(result.point()).isEqualTo(400L);
    }

    @Test
    @DisplayName("다른 유저 동시 포인트 사용 요청은 병렬 처리되어야 한다")
    void givenDifferentUser_whenConcurrentUse_thenRequestsProcessedParallel() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        userPointFacade.chargePoint(10L, 500);
        userPointFacade.chargePoint(20L, 500);


        for (int i = 0; i < threadCount; i++) {
            final long userId = i % 2 == 0 ? 10L : 20L;
            executor.submit(() -> {
                try {
                    userPointFacade.usePoint(userId, 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertThat(userPointTable.selectById(10L).point()).isEqualTo(450L);
        assertThat(userPointTable.selectById(20L).point()).isEqualTo(450L);
    }

}
