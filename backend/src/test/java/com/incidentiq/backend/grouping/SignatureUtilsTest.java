package com.incidentiq.backend.grouping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SignatureUtilsTest {
    @Test
    void messageDifferingOnlyInNumbersHaveSameSignature(){
        String a= SignatureUtils.signature("DB connection timeout after 3142ms on host db-2");
        String b=SignatureUtils.signature("DB connection timeout after 2871ms on host db-1");
        assertEquals(a,b);

    }
    @Test
    void differentMessagesHaveDifferentSignatures(){
        String a=SignatureUtils.signature("DB connection timeout after 3142ms");
        String b=SignatureUtils.signature("Failed To Process Request");
        assertNotEquals(a,b);
    }
    @Test
    void uuidAndIpAreReplacableBeforeNumbers(){
        String result=SignatureUtils.normalize("Request 550e8400-e29b-41d4-a716-446655440000 failed from 10.0.0.5");
        assertEquals("request <uuid> failed from <ip>",result);
    }
    @Test
    void sameMessageWithCapitalisationAndSpacesGiveSameText(){
        String a=SignatureUtils.normalize("Error  CODE 42");
        String b=SignatureUtils.normalize("error code 42");
        assertEquals(a,b);
    }
}
