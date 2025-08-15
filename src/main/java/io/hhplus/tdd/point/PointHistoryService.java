package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> getUserPointHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }


    public PointHistory savePointHistory(long userId, long point, TransactionType type) {
        if(type == null) throw new CustomException(ErrorCodes.INVALID_TRANSACTION_TYPE);
        return pointHistoryTable.insert(userId, point, type, System.currentTimeMillis());
    }
}
