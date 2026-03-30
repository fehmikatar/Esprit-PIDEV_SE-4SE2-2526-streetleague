package tn.esprit._4se2.pi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.PerformanceRequest;
import tn.esprit._4se2.pi.dto.PerformanceResponse;
import tn.esprit._4se2.pi.services.IPerformanceService;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {


    private final IPerformanceService performanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PerformanceResponse createPerformance(@Valid @RequestBody PerformanceRequest request) {
        return performanceService.createPerformance(request);
    }

    @GetMapping
    public List<PerformanceResponse> getAllPerformances() {
        return performanceService.getAllPerformances();
    }

    @GetMapping("/{id}")
    public PerformanceResponse getPerformanceById(@PathVariable Long id) {
        return performanceService.getPerformanceById(id);
    }

    @PutMapping("/{id}")
    public PerformanceResponse updatePerformance(@PathVariable Long id, @Valid @RequestBody PerformanceRequest request) {
        return performanceService.updatePerformance(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerformance(@PathVariable Long id) {
        performanceService.deletePerformance(id);
    }
}