package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.UserNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointTable userPointTable;

    public UserPoint getPoint(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new UserNotExistException();

        return userPoint;
    }

    public UserPoint chargePoint(long id, long chargePointAmount) {
        return userPointTable.selectById(id);
    }
}
