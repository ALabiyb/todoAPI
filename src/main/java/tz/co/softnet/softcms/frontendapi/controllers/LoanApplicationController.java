package tz.co.softnet.softcms.frontendapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tz.co.softnet.softcms.frontendapi.models.LoanApplication;
import tz.co.softnet.softcms.frontendapi.models.ResponseModel;
import tz.co.softnet.softcms.frontendapi.services.LoanApplicationService;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/front-end/loans")
@Tag(name = "Loan Application APIs", description = "Operations related to loan applications")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService service;

    @GetMapping("/")
    @Operation(
            summary = "Home Endpoint",
            description = "Returns a welcome message to show the API is running"
    )
    public ResponseModel<Void> home() {
        return ResponseModel.success(200, "King of Mobile Himself!!!");
    }

    @PostMapping("/create")
    @Operation(summary = "Initialize", description = "Initialize Loan Application")
    public ResponseModel<LoanApplication> createLoan(@RequestBody Map<String, String> request) {
        String nin = request.get("nin");
        int amount = Integer.parseInt(request.get("amount"));
        return service.createLoan(nin, amount);
    }

    @PostMapping("/check-status")
    @Operation(
            summary = "Check Loan Application Status",
            description = "Checks the status of a submitted loan application based on the provided data"
    )
    public ResponseModel<LoanApplication> checkLoanApplicationStatus(@RequestBody Map<String, String> request) {
        String nin = request.get("nin");
        return service.checkLoanStatus(nin);
    }
}
