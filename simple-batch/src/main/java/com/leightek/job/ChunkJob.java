package com.leightek.job;

import com.leightek.batch.LoggingStepStartStopListener;
import com.leightek.batch.RandomChunkSizePolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EnableBatchProcessing
//@SpringBootApplication
public class ChunkJob {

	private final static int ITEM_NUM = 200;
	private final static int CHUNK_SIZE = 20;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.start(chunkStep())
				.build();
	}

	@Bean
	public Step chunkStep() {
		return this.stepBuilderFactory.get("chunkStep")
				.<String, String>chunk(CHUNK_SIZE)
				.reader(itemReader())
				.writer(itemWriter())
				.listener(new LoggingStepStartStopListener())
				.build();
	}

	@Bean
	public ListItemReader<String> itemReader() {
		List<String> items = new ArrayList<>(ITEM_NUM);

		for (int i = 0; i < ITEM_NUM; i++) {
			items.add(UUID.randomUUID().toString());
		}

		return new ListItemReader<>(items);
	}

	@Bean
	public ItemWriter<String> itemWriter() {
		return items -> {
			for (String item : items) {
				System.out.println(">> current item = " + item);
			}
		};
	}

	@Bean
	public CompletionPolicy randomCompletionPolicy() {
		return new RandomChunkSizePolicy();
	}

	public static void main(String[] args) {
		SpringApplication.run(ChunkJob.class, args);
	}

}
