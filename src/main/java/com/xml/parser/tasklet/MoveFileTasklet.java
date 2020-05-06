package com.xml.parser.tasklet;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

public class MoveFileTasklet implements Tasklet {
    private String resourcesPath;

    public String getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    private Resource[] resources;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Collection<StepExecution> stepExecutions = chunkContext.getStepContext().getStepExecution().getJobExecution().getStepExecutions();
        stepExecutions.forEach(stepExecution -> {
            if (stepExecution.getExecutionContext().containsKey("fileName")
                    && ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())
                    && stepExecution.getFailureExceptions().size() <= 0) {
                String file = stepExecution.getExecutionContext().getString("fileName");
                String path = file.replace("file:/", "");
                String[] filename = file.split("/");
                try {
                    FileUtils.moveFile(FileUtils.getFile(path),
                            FileUtils.getFile(resourcesPath+filename[6]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resources, "directory must be set");
    }
}
