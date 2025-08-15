package io.hhplus.tdd.point;


import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.PointAmountInvalidException;
import io.hhplus.tdd.exception.PointBalanceInsufficientException;
import io.hhplus.tdd.exception.UserNotExistException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    UserPointTable userPointTable;

    @InjectMocks
    UserPointService sut;


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

        assertThrows(UserNotExistException.class, () -> sut.getPoint(id));
    }

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

    @DisplayName("아이디와 충천할 포인트양을 전달받아 포인트를 충전할 때, UserPoint 정보가 없는 경우 에러를 리턴한다.")
    @Test
    void givenIdAndPointAmount_whenUserNotExist_thenReturnError() {
        Long id = 1L;
        long chargePointAmount = 1000L;

        when(userPointTable.selectById(id)).thenReturn(null);

        assertThrows(UserNotExistException.class, () -> sut.chargePoint(id, chargePointAmount));
    }


    @DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감한다.")
    @Test
    void givenIdAndPointAmount_whenUsePoint_thenDeductPointAmount() {
        long id = 1L;
        long currentPoint = 1000L;
        long usedPoint = 500L;
        UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
        when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

        UserPoint updatedUserPoint = getUserPointFixture(id, currentPoint - usedPoint);
        when(userPointTable.insertOrUpdate(id, currentPoint - usedPoint)).thenReturn(updatedUserPoint);

        UserPoint result = sut.usePoint(id, usedPoint);

        assertEquals(currentPoint - usedPoint, result.point());
    }

@DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감할 때, UserPoint 정보가 없는 경우 에러를 리턴한다.")
@Test
void givenIdAndPointAmount_whenUserPointNotExist_thenThrowError() {
    long id = 1L;
    long usePoint = 500L;
    when(userPointTable.selectById(id)).thenReturn(null);

    assertThrows(UserNotExistException.class, () -> sut.usePoint(id, usePoint));
}

@DisplayName("아이디와 사용한 포인트양을 전달받아 포인트를 차감할 때, 잔액이 부족하면 에러를 리턴한다.")
@Test
void givenUserIdAndPointAmount_whenBalanceInsufficient_thenThrowError() {
    long id = 1L;
    long currentPoint = 200L;
    long usePoint = 500L;
    UserPoint currentUserPoint = getUserPointFixture(id, currentPoint);
    when(userPointTable.selectById(id)).thenReturn(currentUserPoint);

    assertThrows(PointBalanceInsufficientException.class, () -> sut.usePoint(id, usePoint));
}

@DisplayName("포인트 사용 시, 사용하는 포인트가 0인 경우 예외를 반환한다.")
@Test
void givenIdAndUsePointAmount_whenUsePointAmountIsZero_thenThrowError() {
    long id = 1L;
    long usePoint = 0;

    assertThrows(PointAmountInvalidException.class, () -> sut.usePoint(id, usePoint));
}

@DisplayName("포인트 사용 시, 사용하는 포인트가 0보다 작은 경우 예외를 반환한다.")
@Test
void givenIdAndUsePointAmount_whenUsePointAmountIsLessThenZero_thenThrowError() {
    long id = 1L;
    long usePoint = -5;

    assertThrows(PointAmountInvalidException.class, () -> sut.usePoint(id, usePoint));
}

private UserPoint getUserPointFixture(long id, long point) {
        return new UserPoint(id, point, System.currentTimeMillis());
    }

}
