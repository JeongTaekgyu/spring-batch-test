package com.example.springbatchtest.config;

import com.example.springbatchtest.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomerFileJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // customers.csv 파일을 읽고, 데이터를 가공한 후 저장하는 전체 배치 작업을 정의하는 Job.
    @Bean
    public Job customerFileJob() {
        return new JobBuilder("customerFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(customerFileStep())
                .build();
    }

    // CSV 데이터를 Customer 객체로 변환하고, 가공 후 저장하는 하나의 Step.
    // CSV 파일에서 데이터를 한 줄씩 읽고 (Reader) → 가공하고 (Processor) → 저장하는 (Writer) 처리를 수행하는 Step.
    @Bean
    public Step customerFileStep() {
        return new StepBuilder("customerFileStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customerFileReader())
                .processor(customerProcessor())
                .writer(customerWriter())
                .build();
    }

    // customerFileReader()가 customers.csv 파일을 읽음.
    // CSV 파일의 각 행을 Customer 객체로 매핑함.
    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFileReader(){
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFileReader")
                .resource(new ClassPathResource("customers.csv"))
//                .linesToSkip(1)
                    .delimited()
                .names("id", "name", "email")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Customer.class);
                }})
                .build();
    }

    // customerProcessor()가 각 Customer 객체에 registeredDate 값을 추가함.
    @Bean
    public ItemProcessor<Customer, Customer> customerProcessor() {
        return customer -> {
            customer.setRegisteredDate(LocalDateTime.now());
            return customer;
        };
    }

    // customerWriter()가 가공된 Customer 객체 목록을 받아 로그에 출력함.
    @Bean
    public ItemWriter<Customer> customerWriter() {
        return items -> {
            for (Customer customer : items) {
                log.info("Customer 저장: {}", customer);
            }
        };
    }
}
