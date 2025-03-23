package com.example.springbatchtest;

import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class SpringBatchTestApplicationTests {

    @Test
    void contextLoads() {
    }

    // jobLauncherTestUtils를 Bean으로 등록하여, 테스트 코드에서 간편하게 Job을 실행하고 검증할 수 있도록 도와줌
    @TestConfiguration
    static class TestJobConfig {
        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }
}
