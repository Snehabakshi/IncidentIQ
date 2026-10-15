package com.incidentiq.backend.security;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public final class ApiKeyUtils {
    private static final String KEY_PREFIX="iq_live_";
    private static final SecureRandom SECURE_RANDOM=new SecureRandom();
    private ApiKeyUtils(){

    }
    public static String generateKey(){
        byte[] bytes=new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        String random= Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return KEY_PREFIX + random;
    }
    public static String displayPrefix(String fullkey){
        return fullkey.substring(0,KEY_PREFIX.length() +8);
    }
    public static String hash(String fullkey){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hashBytes=digest.digest(fullkey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        }
        catch(NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
