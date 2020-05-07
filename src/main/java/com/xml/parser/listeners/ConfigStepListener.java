package com.xml.parser.listeners;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ConfigStepListener implements StepExecutionListener {

    @Value("${files.success.path}")
    private String sucessPath;

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        if (!failureExceptions.isEmpty() && failureExceptions.size() > 0) {
            return ExitStatus.UNKNOWN;
        }
        if (stepExecution.getReadCount() == 0) {
            return ExitStatus.UNKNOWN;
        }

        if (stepExecution.getExecutionContext().containsKey("fileName")
                && ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())
                && stepExecution.getFailureExceptions().size() <= 0) {
            String file = stepExecution.getExecutionContext().getString("fileName");
            String path = file.replace("file:/", "");
            String[] filename = file.split("/");
            try {
                FileUtils.moveFile(FileUtils.getFile(path),
                        FileUtils.getFile(sucessPath + filename[4]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return stepExecution.getExitStatus();
    }
}
