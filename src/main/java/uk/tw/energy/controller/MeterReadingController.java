package uk.tw.energy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readings")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    //@PostMapping("/store")
    @PostMapping()
    public ResponseEntity<HttpStatus> storeReadings(@RequestBody MeterReadings meterReadings) {
        if (isMeterReadingsValid(meterReadings)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean isMeterReadingsValid(MeterReadings meterReadings) {
        //String smartMeterId = meterReadings.smartMeterId();
        //List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        /*return smartMeterId != null && !smartMeterId.isEmpty()
                && electricityReadings != null && !electricityReadings.isEmpty();*/
        return !StringUtils.hasLength(meterReadings.smartMeterId()) || CollectionUtils.isEmpty(meterReadings.electricityReadings());
    }

    //@GetMapping("/read/{smartMeterId}")
    @GetMapping("/{smart_meter_id}")
    public ResponseEntity<List<ElectricityReading>> readReadings(@PathVariable("smart_meter_id") String smartMeterId) {
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        return readings.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
