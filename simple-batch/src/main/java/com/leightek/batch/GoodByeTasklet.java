package com.leightek.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class GoodByeTasklet implements Tasklet {

    private static final String GOOD_BYE = "Good bye, %s!";

    @Override
    public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {
        ExecutionContext jobContext = context.getStepContext()
                .getStepExecution()
                .getJobExecution()  // if comment out, to get step context
                .getExecutionContext();

        String name = (String) jobContext.get("user.name");

        System.out.println(String.format(GOOD_BYE, name));
        return RepeatStatus.FINISHED;
    }
}
