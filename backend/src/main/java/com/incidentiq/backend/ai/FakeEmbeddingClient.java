package com.incidentiq.backend.ai;

import com.incidentiq.backend.grouping.SignatureUtils;
import org.springframework.stereotype.Component;

@Component
public class FakeEmbeddingClient implements EmbeddingClient{
    private static final int DIMENSIONS=64;

    @Override
    public float[] embed(String text){
        float [] vector=new float[DIMENSIONS];
        for (String token: SignatureUtils.normalize(text).split("\\s+")){
            vector[Math.floorMod(token.hashCode(),DIMENSIONS)]+=1;
        }
        double length=0;
        for(float v:vector){
            length +=v*v;
        }
        length=Math.sqrt(length);
        if(length>0){
            for(int i=0;i<DIMENSIONS;i++){
                vector[i] /=(float) length;
            }
        }
        return vector;
    }
    @Override
    public int dimensions(){
        return DIMENSIONS;
    }

}
