package tz.co.softnet.softcms.frontendapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tz.co.softnet.softcms.frontendapi.models.LoanApplication;
import tz.co.softnet.softcms.frontendapi.models.ResponseModel;
import tz.co.softnet.softcms.frontendapi.repositories.LoanApplicationRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoanApplicationService {

    @Autowired
    private  RestTemplate restTemplate;

    private String baseUrl;

    @Autowired
    private LoanApplicationRepository repository;

    public LoanApplicationService() {
//        this.restTemplate = restTemplate;
        this.baseUrl = "base_url";
    }

    public ResponseModel<LoanApplication> createLoan(String nin, int amount) {
        try {
            // Prepare request body for POST /api/v1/engine/start-process
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("nidaNumber", nin);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Make POST request
            String url = baseUrl + "/api/v1/engine/start-process";
            ResponseEntity<LoanApplication> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    LoanApplication.class
            );

            // Check response
            if (response.getStatusCode().is2xxSuccessful()) {
                LoanApplication loanApplication = response.getBody();
                // Assuming the external API returns a LoanApplication with updated fields
//                loanApplication.setAmount(amount); // Set amount locally if not returned by API
                return ResponseModel.success(200, "Loan application created successfully", loanApplication);
            } else {
                return ResponseModel.error(response.getStatusCode().value(), "Failed to create loan application");
            }
        } catch (Exception e) {
            return ResponseModel.error(500, "Error creating loan application: " + e.getMessage());
        }
    }

    public ResponseModel<LoanApplication> checkLoanStatus(String nin) {
        try {
            // Prepare GET request with query parameter nidaNumber
            String url = baseUrl + "/api/v1/engine/get-api-result?nidaNumber=" + nin;
            ResponseEntity<LoanApplication> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    LoanApplication.class
            );

            // Check response
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseModel.success(200, "Loan status retrieved successfully", response.getBody());
            } else {
                return ResponseModel.error(response.getStatusCode().value(), "Failed to retrieve loan status");
            }
        } catch (Exception e) {
            return ResponseModel.error(500, "Error retrieving loan status: " + e.getMessage());
        }
    }
}
