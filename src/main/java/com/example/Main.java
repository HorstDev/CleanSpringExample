package com.example;

import com.example.batch.BatchConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(BatchConfig.class);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job importOrdersJob = context.getBean(Job.class);

        Random rand = new Random();
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", rand.nextLong()) // устанавливаем id для каждого запуска
                .addString("fileName", "orders.csv")
                .toJobParameters();

        try {
            jobLauncher.run(importOrdersJob, jobParameters);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Hello World!");
    }
}