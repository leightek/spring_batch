package com.leightek.job;

import com.leightek.batch.GoodByeTasklet;
import com.leightek.batch.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EnableBatchProcessing
//@SpringBootApplication
public class SimpleBatchApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public CompositeJobParametersValidator validator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

		DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
				new String[] {"fileName"}, new String[] {"name", "currentDate"}
		);

		defaultJobParametersValidator.afterPropertiesSet();

		validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));
		return validator;
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.start(step1())
//				.next(step2())
//				.validator(validator())
//				.incrementer(new DailyJobTimestamper())
//				.listener(JobListenerFactoryBean.getListener(new JobLoggerListener()))
				.build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Hello, World!");
					return RepeatStatus.FINISHED;
				})
				.build();
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
				.tasklet(new GoodByeTasklet())
				.build();
	}

	@Bean
	public StepExecutionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();

		listener.setKeys(new String[] {"name"});

		return listener;
	}

	@StepScope
	@Bean
	public Tasklet helloWordTasklet(@Value("#{jobParameters['name']}") String name,
									@Value("#{jobParameters['fileName']}") String fileName) {
		return ((stepContribution, chunkContext) -> {
			System.out.println(String.format("Hello, %s!", name));
			System.out.println(String.format("fileName = %s", fileName));
			return RepeatStatus.FINISHED;
		});
	}

	@StepScope
	@Bean
	public Tasklet helloWorldTasklet() {
		return new HelloWorld();
	}

	public static class HelloWorld implements Tasklet {
		private static final String HELLO_WORLD = "Hello, %s";

		@Override
		public RepeatStatus execute(StepContribution step, ChunkContext context) throws Exception {
			String name = (String) context.getStepContext()
					.getJobParameters()
					.get("name");

			ExecutionContext jobContext = context.getStepContext()
					.getStepExecution()
//					.getJobExecution()  // if comment out, to get step context
					.getExecutionContext();
			jobContext.put("name", name);

			System.out.println(String.format(HELLO_WORLD, name));
			return RepeatStatus.FINISHED;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleBatchApplication.class, args);
	}

}
