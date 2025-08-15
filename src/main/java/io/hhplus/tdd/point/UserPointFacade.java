package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint chargePoint(long id, long chargePoint) {
        return null;
    }

}
