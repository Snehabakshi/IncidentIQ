package com.incidentiq.backend.ai;

import java.util.List;

public final class RunBookCatalog {
    private RunBookCatalog(){}
    public static final List<RunBook>ALL=List.of(
            new RunBook("db-connection-pool","Database connection pool exhausted","Symptoms: pool exhausted, could not obtain connection, connection timeout, database timeout. "+ "Steps: check the maximum pool size, look for slow queries holding connections, "
                    + "check for connection leaks, restart the service if connections are never released."),
            new RunBook("slow-responses","Slow responses and high latency", "Symptoms: response time above threshold, requests timing out, slow endpoint. "
                    + "Steps: check CPU and memory, look at slow database queries, check downstream services, "
                    + "scale out the service if traffic increased."),
            new RunBook("out-of-memory","Out of memory errors","Symptoms: OutOfMemoryError, heap space, container killed, memory usage growing. "
                    + "Steps: check heap settings, take a heap dump, look for memory leaks or large caches, "
                    + "raise the memory limit as a short-term fix."),
            new RunBook("auth-failures","Authentication and authorization failures","Symptoms: unauthorized, forbidden, invalid token, token expired, login failed. "
                    + "Steps: check the token expiry and clock skew, verify the signing key was not rotated, "
                    + "check the identity provider status, review recent permission changes."),
            new RunBook("disk-full","Disk full or no space left","Symptoms: no space left on device, write failed, disk usage above 90 percent. "
                    + "Steps: find the largest directories, rotate and compress old logs, clean temporary files, "
                    + "extend the volume if usage keeps growing.")

    );

}
