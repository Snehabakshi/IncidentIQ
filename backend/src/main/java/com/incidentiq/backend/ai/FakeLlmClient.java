package com.incidentiq.backend.ai;

import java.util.ArrayList;
import java.util.List;

public class FakeLlmClient implements LlmClient{
    @Override
    public String complete(String prompt){
        String cause=null;
        List<String> actions=new ArrayList<>();
        for(String line:prompt.split("\n")){
            if(!line.startsWith("[")) continue;
            int close=line.indexOf("]");
            String ref=line.substring(0,close+1);
            String text=line.substring(close+2);
            if(cause== null){
                cause=text.split("\\. ")[0]+" "+ref;

            }
            int res=text.indexOf("Resolution:");
            int steps=text.indexOf("Steps:");
            if(res>=0){
                actions.add(text.substring(res+ "Resolution:".length()).trim()+" "+ref);

            }
            else if(steps>=0){
                actions.add(text.substring(steps +"Steps:".length()).trim()+" "+ref);
            }
        }
        if(cause== null){
            return "No relevant sources found; I can't suggest a fix.";
        }
        StringBuilder answer=new StringBuilder();
        answer.append("Likely cause: this looks like \"").append(cause).append("\".\n");
        answer.append("Recommended actions:\n");
        for(int i=0;i< actions.size();i++){
            answer.append(i+1).append(". ").append(actions.get(i)).append("\n");
        }
        answer.append("Confidence: ").append(actions.size() >=3 ?"medium":"low").append(" (based on ").append(actions.size()).append(" source(s)).");
        return answer.toString();
    }
}
