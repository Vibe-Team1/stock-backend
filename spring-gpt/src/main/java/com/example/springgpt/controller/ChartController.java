package com.example.springgpt.controller;

import com.example.springgpt.dto.ChartCandleDto;
import com.example.springgpt.domain.ChartCandle;
import com.example.springgpt.enums.Interval;
import com.example.springgpt.service.ChartService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chart")
public class ChartController {
    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/{ticker}")
    public Object getChart(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam String interval
    ) {
        if (!Interval.isValid(interval)) {
            return new ErrorResponse("Invalid interval: " + interval);
        }

        List<ChartCandle> candles = chartService.findByTickerAndRange(ticker, from, to);
        return candles.stream()
                .map(c -> new ChartCandleDto(
                        c.getTicker(), c.getTimestamp(), c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume()
                )).collect(Collectors.toList());
    }

    public record ErrorResponse(String message) {}
}
