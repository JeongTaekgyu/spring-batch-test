package com.example.springbatchtest.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
class HelloJobConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // 배치 Job을 테스트할 수 있도록 도와주는 유틸리티

    @Autowired
    private JobExplorer jobExplorer; // 실행된 Job의 상태를 조회하는 데 사용

    @Autowired
    @Qualifier("helloJob") // 특정 Job을 명시적으로 주입
    private Job helloJob;

    @Test
    void helloJobTest() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .addString("datetime", LocalDateTime.now().toString())
                .toJobParameters();
        jobLauncherTestUtils.setJob(helloJob); // 실행할 job을 지정

        // when
        JobExecution jobExecution =
                jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}