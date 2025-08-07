package com.example.batch;

import com.example.models.Order;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false) //todo если true, то исключения. позже разобраться почему
@EnableBatchProcessing
//@EnableWebMvc
@ComponentScan("com.example") // место, где будут сканироваться компоненты, сервисы и др. бины
@PropertySource("classpath:application.properties")
public class BatchConfig {

    @Value("${database.url}")
    private String url;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    @StepScope  // StepScope потому, что бин создается при старте приложения, а параметры к этому моменту еще не созданы
    public FlatFileItemReader<Order> orderReader(@Value("#{jobParameters['fileName']}") String fileName) {
        return new FlatFileItemReaderBuilder<Order>()
                .name("myReader")//todo константа
                .resource(new ClassPathResource(fileName))
                .linesToSkip(1) // пропускаем заголовок
                .delimited()
                .names("id", "customerName", "orderDate", "amount")
                .fieldSetMapper(new OrderFieldsMapper())
                .build();
    }

    // Вставляет записи в БД
    @Bean
    public JdbcBatchItemWriter<Order> orderWriter(DataSource dataSource) {
        // EXCLUDED - исключенный, т.е. запись, у которой возник конфликт по id
        final String sql = "INSERT INTO orders (id, customer_name, order_date, amount, vip) " +
                "VALUES (:id, :customerName, :orderDate, :amount, :vip) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "customer_name = EXCLUDED.customer_name, " +
                "order_date = EXCLUDED.order_date, " +
                "amount = EXCLUDED.amount, " +
                "vip = EXCLUDED.vip";

        return new JdbcBatchItemWriterBuilder<Order>()
                .sql(sql)
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    // Описываем один шаг и все соединяем
    @Bean
    public Step importOrdersStep(FlatFileItemReader<Order> orderReader, JdbcBatchItemWriter<Order> orderWriter,
                                 JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                 OrderProcessor orderProcessor, OrderSkipPolicy orderSkipPolicy) throws Exception {
        return new StepBuilder("importOrdersStep")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                // Тут указываем типы, из которого будет читаться и в который записываться и размер чанка для step
                .<Order, Order>chunk(3)
                .reader(orderReader)
                .processor(orderProcessor)
                .writer(orderWriter)
                .faultTolerant()
                .skipPolicy(orderSkipPolicy)
                .build();
    }

    @Bean
    public Job importOrdersJob(Step importOrdersStep, JobRepository jobRepository) {
        return new JobBuilder("importOrdersJob")
                .repository(jobRepository)
                .start(importOrdersStep)
                .build();
    }

}
