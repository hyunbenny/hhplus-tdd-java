package io.hhplus.tdd.point;

import io.hhplus.tdd.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint getPoint(long id) {
        return userPointService.getPoint(id);
    }

    public UserPoint chargePoint(long id, long chargePoint) {
        UserPoint originUserPoint = userPointService.getPoint(id);
        try {
            UserPoint chargedUserPoint = userPointService.chargePoint(id, chargePoint);
            pointHistoryService.savePointHistory(id, chargePoint, TransactionType.CHARGE);
            return chargedUserPoint;
        } catch (CustomException e) {
            userPointService.rollback(id, originUserPoint.point());
            throw e;
        }
    }

    public UserPoint usePoint(long id, long usePoint) {
        UserPoint originUserPoint = userPointService.getPoint(id);
        try {
            UserPoint chargedUserPoint = userPointService.usePoint(id, usePoint);
            pointHistoryService.savePointHistory(id, usePoint, TransactionType.USE);
            return chargedUserPoint;
        } catch (CustomException e) {
            userPointService.rollback(id, originUserPoint.point());
            throw e;
        }
    }

    public List<PointHistory> getUserPointHistories(long userId) {
        return pointHistoryService.getUserPointHistories(userId);
    }
}
