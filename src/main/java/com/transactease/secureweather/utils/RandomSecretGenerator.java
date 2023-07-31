package com.transactease.secureweather.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Component
@Slf4j
public class RandomSecretGenerator implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${app.jwtSecretFile}")
    private String jwtSecretFile;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Override
    public void onApplicationEvent( ApplicationReadyEvent event) {
        if (jwtSecret.isEmpty()) {
            jwtSecret = readSecretKey();
            if (jwtSecret.isEmpty()) {
                jwtSecret = generateAndSaveSecret();
            }
        }
    }

    private String readSecretKey() {
        if (!jwtSecretFile.isEmpty()) {
            try {
                byte[] secretBytes = Files.readAllBytes(Paths.get(jwtSecretFile));
                return new String(secretBytes).trim();
            } catch (IOException e) {
                log.error("Failed to read Secret key ");
            }
        }
        return "";
    }

    private String generateAndSaveSecret() {
        byte[] secretBytes = new byte[32];
        new SecureRandom().nextBytes(secretBytes);
        String secretKey = Base64.getEncoder().encodeToString(secretBytes);

        if (!jwtSecretFile.isEmpty()) {
            try {
                Path path = Paths.get(jwtSecretFile);

                Files.write(
                        path,
                        secretKey.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );

                log.info("Successfully created and saved secret key."); // log success

                saveSecretKeyToProperties(secretKey);
                log.info("Successfully saved secret key to properties");
            } catch (IOException e) {
                log.error("Failed to save secret key.", e); // log failure
            }
        }
        return secretKey;
    }

    private void saveSecretKeyToProperties(String secretKey) {
        try {
            Properties prop = new Properties();
            String propFileName = "src/main/resources/application.properties";
            FileInputStream in = new FileInputStream(propFileName);
            prop.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(propFileName);
            prop.setProperty("app.jwtSecret", secretKey);
            prop.store(out, null);
            out.close();
        } catch (IOException e) {
            log.error("Error occurred: ",e);
        }
    }
}
