package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCodes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointTable userPointTable;

    public UserPoint getPoint(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        return userPoint;
    }

    public UserPoint chargePoint(long id, long chargePointAmount) {
        if(chargePointAmount <= 0) throw new CustomException(ErrorCodes.POINT_AMOUNT_INVALID);

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        long chargedPoint = userPoint.point() + chargePointAmount;

        return userPointTable.insertOrUpdate(id, chargedPoint);
    }

    public UserPoint usePoint(long id, long usePointAmount) {
        if(usePointAmount <= 0) throw new CustomException(ErrorCodes.POINT_AMOUNT_INVALID);

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        long balancePoint = userPoint.point() - usePointAmount;
        if(balancePoint < 0) throw new CustomException(ErrorCodes.POINT_BALANCE_INSUFFICIENT);

        return userPointTable.insertOrUpdate(id, balancePoint);
    }

    public UserPoint rollback(long id, long originPoint) {
        return userPointTable.insertOrUpdate(id, originPoint);
    }
}
