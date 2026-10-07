package com.lankatex.smarttextile.common.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {

    // Open the LankaTex home page after the application starts successfully
    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {

        String url = "http://localhost:8080/";

        try {

            String operatingSystem =
                    System.getProperty("os.name").toLowerCase();

            if (operatingSystem.contains("win")) {

                // Windows
                new ProcessBuilder(
                        "cmd",
                        "/c",
                        "start",
                        "",
                        url
                ).start();

            } else if (operatingSystem.contains("mac")) {

                // macOS
                new ProcessBuilder(
                        "open",
                        url
                ).start();

            } else {

                // Linux
                new ProcessBuilder(
                        "xdg-open",
                        url
                ).start();
            }

            System.out.println(
                    "LankaTex browser opened automatically: " + url
            );

        } catch (Exception exception) {

            System.out.println(
                    "Browser could not be opened automatically: "
                            + exception.getMessage()
            );
        }
    }
}