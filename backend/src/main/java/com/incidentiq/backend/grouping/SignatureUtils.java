package com.incidentiq.backend.grouping;

import com.incidentiq.backend.security.ApiKeyUtils;

import java.util.regex.Pattern;

public final class SignatureUtils {
    private static final Pattern UUID=Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    private static final Pattern IP=Pattern.compile("\\b\\d{1,3}(\\.\\d{1,3}){3}\\b");
    private static final Pattern NUMBER=Pattern.compile("\\d+");
    private static final Pattern SPACES=Pattern.compile("\\s+");
    private SignatureUtils(){}
    public static String normalize(String message){
        String s=message.toLowerCase().trim();

        s=UUID.matcher(s).replaceAll("<uuid>");
        s=IP.matcher(s).replaceAll("<ip>");
        s=NUMBER.matcher(s).replaceAll("<num>");
        s=SPACES.matcher(s).replaceAll(" ");
        return s;
    }
    public static String signature(String message){
        return ApiKeyUtils.hash(normalize(message));
    }
}
