package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointHistoryService sut;

    @DisplayName("userId로 포인트 이력을 조회한다.")
    @Test
    void givenUserId_whenGetHistories_thenReturnPointHistoryList() {
        long userId = 1L;
        List<PointHistory> pointHistoryList = getPointHistoryFixtureList();
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(pointHistoryList);

        List<PointHistory> result = sut.getUserPointHistories(1L);

        assertEquals(pointHistoryList.size(), result.size());
        assertEquals(pointHistoryList.get(0).userId(), result.get(0).userId());
        assertEquals(pointHistoryList.get(0).amount(), result.get(0).amount());
        assertEquals(pointHistoryList.get(0).type(), result.get(0).type());
        assertEquals(pointHistoryList.get(pointHistoryList.size() - 1).userId(), result.get(result.size() - 1).userId());
        assertEquals(pointHistoryList.get(pointHistoryList.size() - 1).type(), result.get(result.size() - 1).type());
        assertEquals(pointHistoryList.get(pointHistoryList.size() - 1).amount(), result.get(result.size() - 1).amount());
    }

    @DisplayName("userId로 조회된 값이 없는 경우 빈 리스트를 반환한다.")
    @Test
    void givenUserId_whenNoHistory_thenReturnEmptyList() {
        long userId = 1L;
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<PointHistory> result = sut.getUserPointHistories(1L);

        assertTrue(result.isEmpty());
    }

    private List<PointHistory> getPointHistoryFixtureList() {
        List<PointHistory> fixtures = new ArrayList<>();
        long userId = 1L;

        for (int i = 1; i <= 10; i++) {
            int amount = i * 100;
            TransactionType type = (i % 2 == 0) ? TransactionType.CHARGE : TransactionType.USE;
            fixtures.add(new PointHistory((long)i, userId, amount, type, System.currentTimeMillis()));
        }

        return fixtures;
    }

}
