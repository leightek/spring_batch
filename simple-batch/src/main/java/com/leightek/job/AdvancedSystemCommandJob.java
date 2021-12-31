package com.leightek.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@EnableBatchProcessing
//@SpringBootApplication
public class AdvancedSystemCommandJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("systemCommandJob")
                .start(systemCommandStep())
                .build();
    }

    @Bean
    public Step systemCommandStep() {
        return this.stepBuilderFactory.get("systemCommandStep")
                .tasklet(systemCommandTasklet())
                .build();
    }

    @Bean
    public SystemCommandTasklet systemCommandTasklet() {
        SystemCommandTasklet tasklet = new SystemCommandTasklet();

        tasklet.setCommand("touch D:\\Tmp\\tmp.txt");
        tasklet.setTimeout(5000);
        tasklet.setInterruptOnCancel(true);

        tasklet.setWorkingDirectory("D:\\Tmp");

        tasklet.setSystemProcessExitCodeMapper(touchCodeMapper());
        tasklet.setTerminationCheckInterval(5000);
        tasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
        tasklet.setEnvironmentParams(new String[] {
                "TEST_HOME=/C/Temp",
                "BATCH_HOME=/C/Users/batch"
        });

        return tasklet;
    }

    @Bean
    public SimpleSystemProcessExitCodeMapper touchCodeMapper() {
        return new SimpleSystemProcessExitCodeMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(AdvancedSystemCommandJob.class, args);
    }

}
