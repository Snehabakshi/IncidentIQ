import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
import java.util.Properties;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator{
    static final String BASE_URL="http://localhost:8080";
    static final HttpClient CLIENT=HttpClient.newHttpClient();
    static final String[][] TENANTS = {
            // slug, company name, admin email, admin password, then the services
            {"alpha", "Sim Alpha", "admin@simalpha.com", "alphaPass1234", "payment-api", "checkout-api", "auth-service"},
            {"beta",  "Sim Beta",  "admin@simbeta.com",  "betaPass12345", "orders-api", "inventory-api"}
    };
    static final String[] INFO_MSGS={
            "Request Completed","Health Check ok","Cache Refreshed","User Session Started"
    };
    static final String[] WARN_MSGS={
            "Response time above 500ms","Retrying downstream call","Connection pool at 80% capacity"
    };
    static final String[] ERROR_MSGS={
            "Failed To Process Request","Unexpected null response from upstream"
    };
    static String pick(String [] options){
        return options[ThreadLocalRandom.current().nextInt(options.length)];
    }
    static String randomSeverity(){
        double r= ThreadLocalRandom.current().nextDouble();
        if(r<0.70) return "INFO";
        if(r<0.92) return "WARN";
        return "ERROR";

    };
    static String messageFor(String severity){
        return switch(severity){
            case "INFO"-> pick(INFO_MSGS);
            case "WARN"->pick(WARN_MSGS);
            default-> pick(ERROR_MSGS);
        };
    };
    static String eventJson(String service, String severity, String message) {
        return eventJson(service, severity, message, UUID.randomUUID().toString());
    }

    static String eventJson(String service, String severity, String message, String externalId) {
        return "{\"service\":\"" + service + "\",\"severity\":\"" + severity
                + "\",\"message\":\"" + message + "\",\"externalId\":\"" + externalId + "\"}";
    }

    public static void main(String[] args) throws Exception{
        String mode=args.length>0?args[0]:"help";
        switch(mode){
            case "setup"->setup();
            case "run"-> run(args.length>1 ?Integer.parseInt(args[1]):30);
            case "storm" -> storm(args[1], args[2], Integer.parseInt(args[3]));
            default->System.out.println("Usage: java Simulator.java setup | run");
        }

    }
    static HttpResponse<String> post(String path,String json,String headerName,String headerValue) throws Exception{
        HttpRequest.Builder builder= HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL +path))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if(headerName!=null){
            builder.header(headerName,headerValue);
        }
        return CLIENT.send(builder.build(),HttpResponse.BodyHandlers.ofString());

    }
    static String extract(String json,String field){
        Matcher m =Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find()?m.group(1):null;
    }
    static void setup() throws Exception{
        Properties props=new Properties();
        for (String []t:TENANTS){
            String slug=t[0], company=t[1], email=t[2], password=t[3];
            var signup=post("/api/v1/auth/signup","{\"companyName\":\"" + company + "\",\"email\":\"" + email
                    + "\",\"password\":\"" + password + "\"}", null, null);
            System.out.println("signup " + company + " -> " + signup.statusCode());
            var login=post("/api/v1/auth/login",
                    "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}", null, null);
            String token=extract(login.body(),"accessToken");
            if(token==null){
                System.out.println("login failed for " + email + " -> " + login.statusCode());
                continue;
            }
            for(int i=4;i<t.length;i++){
                String service=t[i];
                var created=post("/api/v1/api-keys","{\"name\":\"" + service + "\"}", "Authorization", "Bearer " + token);
                String apiKey=extract(created.body(),"apiKey");
                if(apiKey!=null){
                    props.setProperty(slug + "/" + service, apiKey);
                    System.out.println("  key created for " + slug + "/" + service);
                }
                else{
                    System.out.println("  FAILED for " + service + " -> " + created.statusCode());
                }
            }
        }
        try (var out = new FileOutputStream("simulator.properties")) {
            props.store(out, "Simulator API keys (gitignored)");
        }
        System.out.println("Saved " + props.size() + " keys to simulator.properties");
    }
    static void run(int seconds) throws Exception{
        Properties props=new Properties();
        try(var in=new FileInputStream("simulator.properties")){
            props.load(in);
        }
        AtomicInteger sent=new AtomicInteger();
        AtomicInteger failed=new AtomicInteger();
        List<Thread>threads=new ArrayList<>();
        long endTime=System.currentTimeMillis()+seconds*1000L;
        for(String name:props.stringPropertyNames()){
            String service=name.substring(name.indexOf('/')+1);
            String apiKey=props.getProperty(name);
            Thread t=new Thread(()->{
                while(System.currentTimeMillis()<endTime){
                    try{
                        String severity=randomSeverity();
                        var response=post("/api/v1/events",eventJson(service,severity,messageFor(severity)),"X-API-KEY",apiKey);
                        if(response.statusCode()==201){
                            sent.incrementAndGet();
                        }
                        else{
                            failed.incrementAndGet();
                        }
                        Thread.sleep(ThreadLocalRandom.current().nextLong(200,1500));
                    }
                    catch(Exception e){
                        failed.incrementAndGet();
                    }
                }
            });
            threads.add(t);
            t.start();
        }
        for(Thread t:threads){
            t.join();
        }
        System.out.println("Done. sent=" + sent + " failed=" + failed);
    }
    static void storm(String slug,String service,int count) throws Exception{
        Properties props=new Properties();
        try(var in= new FileInputStream("simulator.properties")){
            props.load(in);
        }
        String apiKey=props.getProperty(slug+"/"+service);
        if(apiKey==null){
            System.out.println("No key found for " + slug + "/" + service);
            return;
        }
        ThreadLocalRandom rnd=ThreadLocalRandom.current();
        int created=0,duplicates=0,failed=0;
        for(int i=0;i<count;i++){
            String message="DB connection timeout after "+rnd.nextInt(2000,5000)+"ms on host db-"+rnd.nextInt(1,4);
            String json=eventJson(service,"ERROR",message,UUID.randomUUID().toString());
            var response=post("/api/v1/events",json,"X-API-Key",apiKey);
            if(response.statusCode()==201) created++ ; else failed++;
            if(rnd.nextDouble()<0.10){
                var retry=post("/api/v1/events",json,"X-API-Key", apiKey);
                if (retry.statusCode() == 200) duplicates++; else failed++;
            }
            Thread.sleep(rnd.nextLong(5, 40));
        }
        System.out.println("Storm done. created=" + created + " duplicates=" + duplicates + " failed=" + failed);
    }
}