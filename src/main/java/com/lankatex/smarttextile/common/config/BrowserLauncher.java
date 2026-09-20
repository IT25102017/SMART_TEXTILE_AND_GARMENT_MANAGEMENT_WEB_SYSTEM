package com.lankatex.smarttextile.common.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher {

    // Open the application home page after Spring Boot starts successfully
    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {

        try {

            String url = "http://localhost:8080/";

            if (Desktop.isDesktopSupported()) {

                Desktop.getDesktop().browse(
                        new URI(url)
                );

            }

        } catch (Exception exception) {

            System.out.println(
                    "Browser could not be opened automatically: "
                            + exception.getMessage()
            );
        }
    }
}