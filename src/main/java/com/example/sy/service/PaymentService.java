package com.example.sy.service;

import com.example.sy.model.Booking;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface PaymentService {
    String generateEsewaSignature(Map<String, String> params, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException;
    boolean verifyEsewaSignature(Map<String, String> params, String receivedSignature, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException;
            boolean verifyEsewaPayment(String oid, String amount, String refId);

}