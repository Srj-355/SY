package com.example.sy.service.impl;

import com.example.sy.handler.PaymentErrorHandler;
import com.example.sy.model.Booking;
import com.example.sy.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public  class PaymentServiceImpl implements PaymentService {
    private final RestTemplate restTemplate;

    @Value("${khalti.secret-key}")
    private String khaltiSecretKey;

    @Value("${esewa.secret-key}")
    private String eSewaSecretKey;

    @Value("${esewa.test-mode:true}")
    private boolean esewaTestMode;

    public PaymentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setErrorHandler(new PaymentErrorHandler());
    }

    @Override
    public String generateEsewaSignature(Map<String, String> params, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {

        String signedFieldNames = params.get("signed_field_names");
        String[] fieldNames = signedFieldNames.split(",");

        StringBuilder dataBuilder = new StringBuilder();
        for (String fieldName : fieldNames) {
            dataBuilder.append(fieldName).append("=").append(params.get(fieldName)).append(",");
        }

        if (dataBuilder.length() > 0) {
            dataBuilder.setLength(dataBuilder.length() - 1);
        }

        String data = dataBuilder.toString();

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hashBytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    @Override
    public boolean verifyEsewaSignature(Map<String, String> params, String receivedSignature, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {

        String generatedSignature = generateEsewaSignature(params, secretKey);
        return generatedSignature.equals(receivedSignature);
    }
    
    @Override
public boolean verifyEsewaPayment(String oid, String amount, String refId) {
    try {
        // Prepare verification parameters
        Map<String, String> verificationParams = new HashMap<>();
        verificationParams.put("oid", oid);
        verificationParams.put("amt", amount);
        verificationParams.put("refId", refId);
        verificationParams.put("signed_field_names", "oid,amt,refId");

        // Build verification URL based on test/production mode
        String verificationUrl = esewaTestMode 
            ? "https://rc-epay.esewa.com.np/api/epay/main/v2/transactions/verify" 
            : "https://epay.esewa.com.np/api/epay/main/v2/transactions/verify";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + eSewaSecretKey);

        // Create request entity
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(verificationParams, headers);

        // Send verification request
        ResponseEntity<String> response = restTemplate.exchange(
            verificationUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Check response status
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse response to check verification status
            // eSewa returns "SUCCESS" in response body for successful verification
            return response.getBody() != null && response.getBody().contains("\"SUCCESS\"");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}