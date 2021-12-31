package com.leightek.batch;

import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BatchConfiguration {

    @Bean
    public JobParametersValidator jobParametersValidator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();

        validator.setRequiredKeys(new String[] {"fileName"});
        validator.setOptionalKeys(new String[] {"name"});

        return validator;
    }
}
