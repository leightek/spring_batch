package com.leightek.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloTasklet implements Tasklet {

    private static final String HELLO_WORLD = "Hello, %s";

    @Override
    public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {
        String name = (String) context.getStepContext()
                .getJobParameters()
                .get("name");

        ExecutionContext jobContext = context.getStepContext()
                .getStepExecution()
                .getJobExecution()  // if comment out, to get step context
                .getExecutionContext();
        jobContext.put("user.name", name);

        System.out.println(String.format(HELLO_WORLD, name));
        return RepeatStatus.FINISHED;
    }
}
