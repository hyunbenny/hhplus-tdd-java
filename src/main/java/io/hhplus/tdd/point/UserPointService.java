package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCodes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserPointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getPoint(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        return userPoint;
    }

    // TODO: 실제 DB를 붙이는 경우 try-catch에서 트랜잭션 처리하는 걸 @Transactional 을 통해 처리하도록 수정 필요
    public UserPoint chargePoint(long id, long chargePointAmount) {
        if(chargePointAmount <= 0) throw new CustomException(ErrorCodes.POINT_AMOUNT_INVALID);

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        long originPointAmount = userPoint.point();
        long chargedPoint = userPoint.point() + chargePointAmount;

        UserPoint updatedUserPoint = null;
        try {
            updatedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);
            pointHistoryTable.insert(id, chargePointAmount, TransactionType.CHARGE, System.currentTimeMillis());
        } catch (Exception e) {
            rollback(id, originPointAmount);
        }

        return updatedUserPoint;
    }

    // TODO: 실제 DB를 붙이는 경우 try-catch에서 트랜잭션 처리하는 걸 @Transactional 을 통해 처리하도록 수정 필요
    public UserPoint usePoint(long id, long usePointAmount) {
        if(usePointAmount <= 0) throw new CustomException(ErrorCodes.POINT_AMOUNT_INVALID);

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint == null) throw new CustomException(ErrorCodes.USER_NOT_EXIST);

        long originPointAmount = userPoint.point();
        long balancePoint = userPoint.point() - usePointAmount;
        if(balancePoint < 0) throw new CustomException(ErrorCodes.POINT_BALANCE_INSUFFICIENT);

        UserPoint updatedUserPoint = null;
        try {
            updatedUserPoint = userPointTable.insertOrUpdate(id, balancePoint);
            pointHistoryTable.insert(id, balancePoint, TransactionType.USE, System.currentTimeMillis());
        } catch (Exception e) {
            rollback(id, originPointAmount);
        }

        return updatedUserPoint;
    }

    public UserPoint rollback(long id, long originPoint) {
        return userPointTable.insertOrUpdate(id, originPoint);
    }
}
