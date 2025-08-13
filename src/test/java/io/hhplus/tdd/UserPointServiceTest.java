package io.hhplus.tdd;


import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.UserNotExistException;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointService;
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

    private UserPoint getUserPointFixture(long id, long point) {
        return new UserPoint(id, point, System.currentTimeMillis());
    }

}
