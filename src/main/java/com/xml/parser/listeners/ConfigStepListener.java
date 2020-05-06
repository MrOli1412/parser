package com.xml.parser.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.List;

public class ConfigStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<Throwable> failureExceptions = stepExecution.getFailureExceptions();

        if (!failureExceptions.isEmpty() && failureExceptions.size() > 0) {
            return ExitStatus.UNKNOWN;
        }
        if (stepExecution.getReadCount() == 0) {
            return ExitStatus.UNKNOWN;
        }

        return stepExecution.getExitStatus();
    }
}
