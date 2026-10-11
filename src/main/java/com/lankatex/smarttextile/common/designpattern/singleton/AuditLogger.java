package com.lankatex.smarttextile.common.designpattern.singleton;

import java.time.LocalDateTime;

// Singleton logger used to record audit messages
public class AuditLogger {

    // Stores the single logger instance
    private static AuditLogger instance;

    // Private constructor prevents creating multiple objects
    private AuditLogger() {
    }

    // Returns the same logger instance every time
    public static synchronized AuditLogger getInstance() {

        if (instance == null) {
            instance = new AuditLogger();
        }

        return instance;
    }

    // Prints the audit message with date and time
    public void log(String message) {
        System.out.println(
                "[AUDIT] " + LocalDateTime.now() + " - " + message
        );
    }
}