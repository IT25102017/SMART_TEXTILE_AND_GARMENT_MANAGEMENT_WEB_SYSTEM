package com.lankatex.smarttextile.common.designpattern.singleton;

import java.time.LocalDateTime;

public class AuditLogger {

    private static AuditLogger instance;

    private AuditLogger() {
    }

    public static synchronized AuditLogger getInstance() {

        if (instance == null) {
            instance = new AuditLogger();
        }

        return instance;
    }

    public void log(String message) {
        System.out.println(
                "[AUDIT] " + LocalDateTime.now() + " - " + message
        );
    }
}
