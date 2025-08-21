package io.hhplus.tdd.point;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    UserPointTable userPointTable;

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    UserPointService sut;

    @Nested
    @DisplayName("UserPoint 조회")
    class getUserPoint {
        @DisplayName("아이디를 전달받아 UserPoint를 조회한다.")
        @Test
        void givenUserId_whenPoint_thenReturnUserPoint() {
            Long id = 1L;
            Long point = 100L;
            UserPoint userPoint = getUserPointFixture(id, point);
            when(userPointTable.selectById(id)).thenReturn(userPoint);

            UserPoint result = sut.getPoint(id);

            assertEquals(userPoint.id(), result.id());
            assertEquals(userPoint.point(), result.point());
        }

        @DisplayName("UserPoint를 조회할 때 데이터가 없는 경우, 에러를 리턴한다.")
        @Test
        void givenUserId_whenUserNotExist_thenReturnError() {
            Long id = 1L;

            when(userPointTable.selectById(id)).thenReturn(null);

            assertThrows(CustomException.class, () -> sut.getPoint(id));
        }
    }


    @DisplayName("포인트 충전")
    @Nested
    class chargePoint {
        @DisplayName("아이디와 충천할 포인트양을 전달받아 포인트를 충전한다.")
        @Test
        void givenIdAndPointAmount_whenChargePoint_thenAddPointAmount() {
            long id = 1L;
            long currentPoint = 500L;
            long chargePointAmount = 1000L;
            long sum = currentPoint + chargePointAmount;

            UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
            when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

            UserPoint updatedUserPoint = getUserPointFixture(id, sum);
            when(userPointTable.insertOrUpdate(id, sum)).thenReturn(updatedUserPoint);

            UserPoint result = sut.chargePoint(id, chargePointAmount);

            assertEquals(sum, result.point());
        }

        @DisplayName("아이디와 충천할 포인트양을 전달받아 포인트를 충전하고 이력을 저장한다.")
        @Test
        void givenIdAndPointAmount_whenChargePoint_thenAddPointAmountAndSaveHistory() {
            long id = 1L;
            long currentPoint = 500L;
            long chargePointAmount = 1000L;
            long sum = currentPoint + chargePointAmount;

            UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
            when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

            UserPoint updatedUserPoint = getUserPointFixture(id, sum);
            when(userPointTable.insertOrUpdate(id, sum)).thenReturn(updatedUserPoint);

            UserPoint result = sut.chargePoint(id, chargePointAmount);

            assertEquals(sum, result.point());
            verify(pointHistoryTable, times(1)).insert(eq(id), eq(chargePointAmount), eq(TransactionType.CHARGE), anyLong());
        }

        @Test
        @DisplayName("아이디와 충천할 포인트양을 전달받아 포인트를 충전하고 이력 저장 중 싪패하면 롤백이 되어야 한다.")
        void givenIdAndPointAmount_whenAfterChargePoint_PointHistoryTableOccurDBException_thenRollbackUserPoint() {
            long id = 1L;
            long currentPointAmount = 500L;
            long chargePointAmount = 1000L;
            long sum = currentPointAmount + chargePointAmount;

            UserPoint userPoint = getUserPointFixture(id, currentPointAmount);
            UserPoint updatedUserPoint = getUserPointFixture(id, sum);

            when(userPointTable.selectById(id)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(id, sum)).thenReturn(updatedUserPoint);

            doThrow(new RuntimeException("DB error"))
                    .when(pointHistoryTable)
                    .insert(eq(id), eq(sum), eq(TransactionType.CHARGE), anyLong());

            UserPointService spyService = spy(sut);
            spyService.chargePoint(id, chargePointAmount);

            verify(spyService, times(1)).rollback(id, currentPointAmount);
        }

        @DisplayName("아이디와 충천할 포인트양을 전달받아 포인트를 충전할 때, UserPoint 정보가 없는 경우 에러를 리턴한다.")
        @Test
        void givenIdAndPointAmount_whenUserNotExist_thenReturnError() {
            Long id = 1L;
            long chargePointAmount = 1000L;

            when(userPointTable.selectById(id)).thenReturn(null);

            CustomException exception = assertThrows(CustomException.class, () -> sut.chargePoint(id, chargePointAmount));
            assertEquals(ErrorCodes.USER_NOT_EXIST.getCode(), exception.getErrorCode());
        }

        @DisplayName("충전하는 포인트가 0인 경우 예외를 반환한다.")
        @Test
        void givenIdAndChargePointAmount_whenChargePointAmountIsZero_thenThrowError() {
            long id = 1L;
            long chargePoint = 0L;

            CustomException exception = assertThrows(CustomException.class, () -> sut.chargePoint(id, chargePoint));
            assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), exception.getErrorCode());
        }

        @DisplayName("충전하는 포인트가 0보다 작은 경우 예외를 반환한다.")
        @Test
        void givenIdAndChargePointAmount_whenChargePointAmountIsLessThenZero_thenThrowError() {
            long id = 1L;
            long chargePoint = -5L;

            CustomException exception = assertThrows(CustomException.class, () -> sut.chargePoint(id, chargePoint));
            assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), exception.getErrorCode());
        }

    }

    @DisplayName("포인트 사용")
    @Nested
    class usePoint {
        @DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감한다.")
        @Test
        void givenIdAndPointAmount_whenUsePoint_thenDeductPointAmount() {
            long id = 1L;
            long currentPoint = 1000L;
            long usePoint = 500L;
            UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
            when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

            UserPoint updatedUserPoint = getUserPointFixture(id, currentPoint - usePoint);
            when(userPointTable.insertOrUpdate(id, currentPoint - usePoint)).thenReturn(updatedUserPoint);

            UserPoint result = sut.usePoint(id, usePoint);

            assertEquals(currentPoint - usePoint, result.point());
        }

        @DisplayName("아이디와 사용할 포인트양을 전달받아 포인트를 사용하고 이력을 저장한다.")
        @Test
        void givenIdAndPointAmount_whenUsePoint_thenDeductPointAmountAndSaveHistory() {
            long id = 1L;
            long currentPoint = 1000L;
            long usePoint = 500L;
            long balance = currentPoint - usePoint;


            UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
            when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

            UserPoint updatedUserPoint = getUserPointFixture(id, balance);
            when(userPointTable.insertOrUpdate(id, balance)).thenReturn(updatedUserPoint);

            UserPoint result = sut.usePoint(id, usePoint);
            verify(pointHistoryTable, times(1)).insert(eq(id), eq(usePoint), eq(TransactionType.USE), anyLong());

            assertEquals(balance, result.point());
        }

        @Test
        @DisplayName("아이디와 사용할 포인트양을 전달받아 포인트를 사용하고 이력 저장 중 싪패하면 롤백이 되어야 한다.")
        void givenIdAndPointAmount_whenAfterUsePoint_PointHistoryTableOccurDBException_thenRollbackUserPoint() {
            long id = 1L;
            long currentPointAmount = 1000L;
            long usePointAmount = 500L;
            long deduct = currentPointAmount - usePointAmount;

            UserPoint userPoint = getUserPointFixture(id, currentPointAmount);
            UserPoint updatedUserPoint = getUserPointFixture(id, deduct);

            when(userPointTable.selectById(id)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(id, deduct)).thenReturn(updatedUserPoint);

            doThrow(new RuntimeException("DB error"))
                    .when(pointHistoryTable)
                    .insert(eq(id), eq(deduct), eq(TransactionType.USE), anyLong());

            UserPointService spyService = spy(sut);
            spyService.usePoint(id, usePointAmount);

            verify(spyService, times(1)).rollback(id, currentPointAmount);
        }

        @DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감할 때, UserPoint 정보가 없는 경우 에러를 리턴한다.")
        @Test
        void givenIdAndPointAmount_whenUserPointNotExist_thenThrowError() {
            long id = 1L;
            long usePoint = 500L;
            when(userPointTable.selectById(id)).thenReturn(null);

            CustomException exception = assertThrows(CustomException.class, () -> sut.usePoint(id, usePoint));
            assertEquals(ErrorCodes.USER_NOT_EXIST.getCode(), exception.getErrorCode());
        }

        @DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감할 때, 잔액이 부족하면 에러를 리턴한다.")
        @Test
        void givenUserIdAndPointAmount_whenBalanceInsufficient_thenThrowError() {
            long id = 1L;
            long currentPoint = 200L;
            long usePoint = 500L;
            UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
            when(userPointTable.selectById(id)).thenReturn(currentUserPoint);


            CustomException exception = assertThrows(CustomException.class, () -> sut.usePoint(id, usePoint));
            assertEquals(ErrorCodes.POINT_BALANCE_INSUFFICIENT.getCode(), exception.getErrorCode());
        }

        @DisplayName("사용하는 포인트가 0인 경우 예외를 반환한다.")
        @Test
        void givenIdAndUsePointAmount_whenUsePointAmountIsZero_thenThrowError() {
            long id = 1L;
            long usePoint = 0;

            CustomException exception = assertThrows(CustomException.class, () -> sut.usePoint(id, usePoint));
            assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), exception.getErrorCode());
        }

        @DisplayName("사용하는 포인트가 0보다 작은 경우 예외를 반환한다.")
        @Test
        void givenIdAndUsePointAmount_whenUsePointAmountIsLessThenZero_thenThrowError() {
            long id = 1L;
            long usePoint = -5;

            CustomException exception = assertThrows(CustomException.class, () -> sut.usePoint(id, usePoint));
            assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), exception.getErrorCode());
        }
    }

    @DisplayName("전달받은 UserPoint로 데이터를 업데이트(롤백)한다.")
    @Test
    void givenUserPoint_whenRollback_thenInsertOrUpdateAndReturnUserPoint() {
        long id = 1L;
        long originPoint = 100L;
        UserPoint beforeUserPoint = getUserPointFixture(id, originPoint);

        when(userPointTable.insertOrUpdate(id, originPoint)).thenReturn(beforeUserPoint);

        UserPoint result = sut.rollback(id, originPoint);

        assertEquals(id, result.id());
        assertEquals(originPoint, result.point());
    }

    private UserPoint getUserPointFixture(long id, long point) {
        return new UserPoint(id, point, System.currentTimeMillis());
    }

    private PointHistory getPointHistoryFixture(TransactionType type) {
        return new PointHistory(1L, 1L, 100L, type, System.currentTimeMillis());
    }

}
