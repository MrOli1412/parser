package com.xml.parser.config;


import com.xml.parser.listeners.ConfigJobListener;
import com.xml.parser.listeners.ConfigStepListener;
import com.xml.parser.model.Config;
import com.xml.parser.step.FileProcessor;
import com.xml.parser.step.FileReader;
import com.xml.parser.step.FileWriter;
import com.xml.parser.tasklet.MoveErrorFileTasklet;
import com.xml.parser.tasklet.MoveFileTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.processing.Processor;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${files.path}")
    private String resourcesPath;
    @Value("${files.error.path}")
    private String errorPath;
    @Value("${files.success.path}")
    private String sucessPath;
    @Value("${files.zip.path}")
    private String zipPath;
    @Value("${files.type}")
    private String fileType;


    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    final
    FileReader fileReader;

    @Autowired
    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, FileReader fileReader) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.fileReader = fileReader;
    }


//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("job")
//                .flow(step1())
//                .end().build();
//    }
//
//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
//                .<Config, Config>chunk(2)
//                .reader(reader())
//                .processor(new FileProcessor())
//                .writer(new FileWriter())
//                .allowStartIfComplete(true)
//                .build();
//    }
//
//    @Bean
//    public StaxEventItemReader<Config> reader() {
//        Resource[] resources = null;
//        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
//        try {
//            resources = patternResolver.getResources("classpath:/static/*.xml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        StaxEventItemReader<Config> reader = new StaxEventItemReader<>();
//        reader.setFragmentRootElementName("root");
//        reader.setResource(resources[0]);
//
//        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setClassesToBeBound(Config.class);
//
//        reader.setUnmarshaller(marshaller);
//        System.out.println("In");
//        return reader;
//    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setCorePoolSize(5);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    Job job() throws Exception {
        return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).listener(new ConfigJobListener())
                .start(masterStep()).on("COMPLETE").to(moveFiles())
                .from(masterStep()).on("UNKNOWN").to(moveErrorFiles()).end().build();
    }



    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get("masterStep").partitioner(slaveStep()).partitioner("partition", partitioner())
                .taskExecutor(taskExecutor()).listener(new ConfigStepListener()).build();
    }

    @Bean
    public Step slaveStep() throws Exception {
        return stepBuilderFactory.get("slaveStep").<Config, Config>chunk(1)
                .reader(reader(null)).processor(processor(null, null)).writer(new FileWriter()).build();
    }

    @Bean
    protected Step moveFiles() {
        MoveFileTasklet moveFilesTasklet = new MoveFileTasklet();
        try{
            moveFilesTasklet.setResourcesPath(sucessPath);
            moveFilesTasklet.setResources(new PathMatchingResourcePatternResolver().getResources("file:" + sucessPath + fileType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stepBuilderFactory.get("moveFiles").tasklet(moveFilesTasklet).build();
    }
    @Bean
    protected Step moveErrorFiles() {
        MoveErrorFileTasklet moveErrorFileTasklet = new MoveErrorFileTasklet();
        try{
            moveErrorFileTasklet.setResourcesPath(errorPath);
            moveErrorFileTasklet.setResources(new PathMatchingResourcePatternResolver().getResources("file:" + errorPath + fileType));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stepBuilderFactory.get("moveErrorFiles").tasklet(moveErrorFileTasklet).build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Config> reader(@Value("#{stepExecutionContext['fileName']}") String fileName) throws MalformedURLException {
        StaxEventItemReader<Config> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("root");
        reader.setResource(new UrlResource(fileName));

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Config.class);

        reader.setUnmarshaller(marshaller);
        System.out.println("In");
        return reader;
    }

    @Bean
    @StepScope
    public FileProcessor processor(@Value("#{stepExecutionContext['fileName']}") String file, @Value("#{stepExecution.jobExecution.id}") String jobID) {
        String fileName = file.substring(file.lastIndexOf("/") + 1);
        FileProcessor fileProcessor = new FileProcessor();
        fileProcessor.setProcessingFileName(fileName);
        fileProcessor.setProcessingJobid(jobID);
        return fileProcessor;

    }

    @Bean
    @JobScope
    public Partitioner partitioner() throws Exception {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        partitioner.setResources(resolver.getResources("file:" + resourcesPath  + fileType));
        partitioner.partition(2);
        System.out.println("--------partitioner()---end -No of files--->" + resolver.getResources("file:" + resourcesPath  + fileType).length);
        return partitioner;
    }


}
