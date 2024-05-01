package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.AccountService;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public final static String PRICE_PLAN_ID_KEY = "pricePlanId";
    public final static String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @GetMapping("/comparisons/{smart_meter_id}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable("smart_meter_id") String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
        return consumptionsForPricePlans.map(consumptionForPricePlan -> getCostForEachPricePlan(pricePlanId, consumptionForPricePlan))
                                    .orElseGet(() -> ResponseEntity.notFound().build());

        /*String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        if (consumptionsForPricePlans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

        return ResponseEntity.ok(pricePlanComparisons);*/

    }

    @GetMapping("/recommendations/{smart_meter_id}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(@PathVariable("smart_meter_id") String smartMeterId,
                                                                                           @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        /*if (consumptionsForPricePlans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(consumptionsForPricePlans.get().entrySet());
        recommendations.sort(Map.Entry.comparingByValue());

        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }

        return ResponseEntity.ok(recommendations);*/

        return consumptionsForPricePlans.map(consumptionsForPricePlan -> getRecommendations(limit, consumptionsForPricePlan))
                                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Map<String, Object>> getCostForEachPricePlan(String pricePlanId, Map<String, BigDecimal> consumptionsForPricePlans) {
        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans);
        return ResponseEntity.ok(pricePlanComparisons);
    }

    private ResponseEntity<List<Map.Entry<String, BigDecimal>>> getRecommendations(Integer limit, Map<String, BigDecimal> e) {
        List<Map.Entry<String, BigDecimal>> recommendations = new ArrayList<>(e.entrySet());
        recommendations.sort(Map.Entry.comparingByValue());
        if (limit != null && limit < recommendations.size()) {
            recommendations = recommendations.subList(0, limit);
        }
        return ResponseEntity.ok(recommendations);
    }
}
