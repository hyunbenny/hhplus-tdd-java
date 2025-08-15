package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    private final boolean fair = true;

    public UserPoint getPoint(long id) {
        return userPointService.getPoint(id);
    }

    public UserPoint chargePoint(long id, long chargePoint) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, k -> new ReentrantLock(fair));
        lock.lock();

        UserPoint originUserPoint = userPointService.getPoint(id);
        try {
            UserPoint chargedUserPoint = userPointService.chargePoint(id, chargePoint);
            pointHistoryService.savePointHistory(id, chargePoint, TransactionType.CHARGE);
            return chargedUserPoint;
        } catch (CustomException e) {
            userPointService.rollback(id, originUserPoint.point());
            throw e;
        } finally {
            lock.unlock();
        }

    }

    public UserPoint usePoint(long id, long usePoint) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, k -> new ReentrantLock(fair));
        lock.lock();

        UserPoint originUserPoint = userPointService.getPoint(id);
        try {
            UserPoint chargedUserPoint = userPointService.usePoint(id, usePoint);
            pointHistoryService.savePointHistory(id, usePoint, TransactionType.USE);
            return chargedUserPoint;
        } catch (CustomException e) {
            userPointService.rollback(id, originUserPoint.point());
            throw e;
        } finally {
            lock.unlock();
        }

    }

    public List<PointHistory> getUserPointHistories(long userId) {
        return pointHistoryService.getUserPointHistories(userId);
    }
}
