package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO: UserPoint / PointHistory를 바로 반환하지 않고 dto를 변환하여 반환
@Component
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint getPoint(long id) {
        return userPointService.getPoint(id);
    }

    public UserPoint chargePoint(long id, long chargePoint) {
        return userPointService.chargePoint(id, chargePoint);
    }

    public UserPoint usePoint(long id, long usePoint) {
        return userPointService.usePoint(id, usePoint);
    }

    public List<PointHistory> getUserPointHistories(long userId) {
        return pointHistoryService.getUserPointHistories(userId);
    }
}
