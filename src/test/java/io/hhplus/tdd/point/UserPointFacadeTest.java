package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class UserPointFacadeTest {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setup() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();

        userPointTable.insertOrUpdate(1L, 100);
    }

    @Test
    @DisplayName("포인트 충전 > 정상 동작")
    void givenUser_whenChargePoint_thenPointIncreased() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService realPointHistoryService = new PointHistoryService(pointHistoryTable);

        UserPointFacade sut = new UserPointFacade(realUserPointService, realPointHistoryService);

        UserPoint result = sut.chargePoint(1L, 500);
        assertEquals(600, result.point());

        List<PointHistory> pointHistoryList = pointHistoryTable.selectAllByUserId(result.id());
        assertEquals(1L, pointHistoryList.size());
        assertEquals(TransactionType.CHARGE, pointHistoryList.get(0).type());
        assertEquals(500, pointHistoryList.get(0).amount());
    }

    @Test
    @DisplayName("포인트 충전 > History 저장 중 예외 발생 시 롤백")
    void givenUser_whenHistoryFails_thenPointRolledBack() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService mockHistoryService = mock(PointHistoryService.class);

        doThrow(new CustomException(ErrorCodes.INVALID_TRANSACTION_TYPE)).when(mockHistoryService).savePointHistory(eq(1L), anyLong(), any());

        UserPointFacade sut = new UserPointFacade(realUserPointService, mockHistoryService);

        CustomException ex = assertThrows(CustomException.class, () -> sut.chargePoint(1L, 500));
        assertEquals(ErrorCodes.INVALID_TRANSACTION_TYPE.getCode(), ex.getErrorCode());

        UserPoint userPoint = userPointTable.selectById(1L);
        assertEquals(100, userPoint.point());
    }

    @Test
    @DisplayName("포인트 충전 > 잘못된 금액 입력 시 예외 발생")
    void givenInvalidAmount_whenCharge_thenThrowsException() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService mockHistoryService = mock(PointHistoryService.class);

        UserPointFacade sut = new UserPointFacade(realUserPointService, mockHistoryService);

        CustomException ex = assertThrows(CustomException.class, () -> sut.chargePoint(1L, -50));

        assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), ex.getErrorCode());
        assertEquals(100, userPointTable.selectById(1L).point());
    }

    @Test
    @DisplayName("포인트 사용 > 정상 동작")
    void givenUser_whenUsePoint_thenPointDecreased() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService realPointHistoryService = new PointHistoryService(pointHistoryTable);

        UserPointFacade sut = new UserPointFacade(realUserPointService, realPointHistoryService);

        UserPoint result = sut.usePoint(1L, 50);
        assertEquals(50, result.point());

        List<PointHistory> pointHistoryList = pointHistoryTable.selectAllByUserId(result.id());
        assertEquals(1L, pointHistoryList.size());
        assertEquals(TransactionType.USE, pointHistoryList.get(0).type());
        assertEquals(50, pointHistoryList.get(0).amount());
    }

    @Test
    @DisplayName("포인트 사용 > History 저장 중 예외 발생 시 롤백")
    void givenUser_whenUsePointHistoryFails_thenPointRolledBack() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService mockHistoryService = mock(PointHistoryService.class);

        doThrow(new CustomException(ErrorCodes.INVALID_TRANSACTION_TYPE)).when(mockHistoryService).savePointHistory(eq(1L), anyLong(), any());

        UserPointFacade sut = new UserPointFacade(realUserPointService, mockHistoryService);

        CustomException ex = assertThrows(CustomException.class, () -> sut.usePoint(1L, 50));
        assertEquals(ErrorCodes.INVALID_TRANSACTION_TYPE.getCode(), ex.getErrorCode());

        UserPoint userPoint = userPointTable.selectById(1L);
        assertEquals(100, userPoint.point());
    }

    @Test
    @DisplayName("포인트 사용 > 잘못된 금액 입력 시 예외 발생")
    void givenInvalidAmount_whenUsePoint_thenThrowsException() {
        UserPointService realUserPointService = new UserPointService(userPointTable);
        PointHistoryService mockHistoryService = mock(PointHistoryService.class);

        UserPointFacade sut = new UserPointFacade(realUserPointService, mockHistoryService);

        CustomException ex = assertThrows(CustomException.class, () -> sut.usePoint(1L, -50));

        assertEquals(ErrorCodes.POINT_AMOUNT_INVALID.getCode(), ex.getErrorCode());
        assertEquals(100, userPointTable.selectById(1L).point());
    }
}