package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.PointReqDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final UserPointFacade userPointFacade;

    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return userPointFacade.getPoint(id);
    }


    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return userPointFacade.getUserPointHistories(id);
    }

    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody PointReqDto req
            ) {
        return userPointFacade.chargePoint(id, req.amount());
    }


    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody PointReqDto req
    ) {
        return userPointFacade.usePoint(id, req.amount());
    }
}
