package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertEquals(Optional.empty(), meterReadingService.getReadings("unknown-id"));
    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        meterReadingService.storeReadings("random-id", new ArrayList<>());
        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));
    }
}
