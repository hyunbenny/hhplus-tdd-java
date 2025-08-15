package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.PointAmountInvalidException;
import io.hhplus.tdd.exception.PointBalanceInsufficientException;
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
        if(chargePointAmount <= 0) throw new PointAmountInvalidException();

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new UserNotExistException();

        long chargedPoint = userPoint.point() + chargePointAmount;

        return userPointTable.insertOrUpdate(id, chargedPoint);
    }

    public UserPoint usePoint(long id, long usePointAmount) {
        if(usePointAmount <= 0) throw new PointAmountInvalidException();

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new UserNotExistException();

        long balancePoint = userPoint.point() - usePointAmount;
        if(balancePoint < 0) throw new PointBalanceInsufficientException();

        return userPointTable.insertOrUpdate(id, balancePoint);
    }
}
