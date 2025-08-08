package com.example.batch;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

// В SkipPolicy пропускаем строки при выброшенном исключении
@Component
public class OrderSkipPolicy implements SkipPolicy {

    private final String LOG_FILE_PATH = "logs/log.txt";

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        try(FileWriter fw = new FileWriter(LOG_FILE_PATH, true)) {
            fw.write("[" + LocalDateTime.now() + "] - " + t.getMessage() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
