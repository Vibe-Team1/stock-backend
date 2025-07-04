package com.hts.chart;

import com.hts.common.exception.ErrorResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chart")
@CrossOrigin(origins = "*")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<?> getChartData(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "1m") String interval) {

        if (!ChartInterval.isValid(interval)) {
            ErrorResponse error = new ErrorResponse(
                "Invalid interval value",
                "Valid intervals are: " + String.join(", ", ChartInterval.getValidValues())
            );
            return ResponseEntity.badRequest().body(error);
        }

        ChartInterval chartInterval = ChartInterval.fromString(interval);
        List<ChartDataResponse> chartData = chartService.getChartData(ticker, from, to, chartInterval);

        return ResponseEntity.ok(chartData);
    }
} 