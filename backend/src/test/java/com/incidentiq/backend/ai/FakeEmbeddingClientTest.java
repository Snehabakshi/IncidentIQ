package com.incidentiq.backend.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeEmbeddingClientTest {
    private final FakeEmbeddingClient client=new FakeEmbeddingClient();
    private double cosine (float []a,float[] b){
        double dot=0;
        for(int i=0;i<a.length;i++){
            dot+=a[i] * b[i];
        }
        return dot;
    }
    @Test
    void similarMessagesAreCloser(){
        float[]a=client.embed("DB Connection timeout after 3142ms on host db-2");
        float[]b=client.embed("Database connection timed out on host db-1");
        float[]c=client.embed("User session started");
        double similarToA=cosine(a,b);
        double unrelatedToA=cosine(a,c);
        System.out.println("a vs b (similar):   " + similarToA);
        System.out.println("a vs c (unrelated): " + unrelatedToA);
        assertTrue(similarToA>unrelatedToA);

    }
    @Test
    void sameTextGivesSameVector(){
        float[] first = client.embed("Failed To Process Request");
        float[] second = client.embed("Failed To Process Request");
        assertArrayEquals(first, second);
    }

}
