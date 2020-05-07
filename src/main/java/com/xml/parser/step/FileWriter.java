package com.xml.parser.step;

import com.xml.parser.model.Config;
import com.xml.parser.model.Element;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.List;

public class FileWriter implements ItemWriter<Config> {
    private String processingJobid;
    /**
     * holds theProcessing File Name
     */
    private String processingFileName;

    /**
     * @return the processingJobid
     */
    public String getProcessingJobid() {
        return processingJobid;
    }

    /**
     * @param processingJobid the processingJobid to set
     */
    public void setProcessingJobid(String processingJobid) {
        this.processingJobid = processingJobid;
    }

    /**
     * @return the processingFileName
     */
    public String getProcessingFileName() {
        return processingFileName;
    }

    public void setProcessingFileName(String processingFileName) {
        this.processingFileName = processingFileName;
    }

    @Override
    public void write(List<? extends Config> list) throws Exception {

        StaxEventItemWriter<Element> writer = new StaxEventItemWriter<>();
        writer.setResource(new UrlResource(getProcessingFileName()));
        writer.setRootTagName("root");
        writer.setOverwriteOutput(true);
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Element.class);
        writer.setMarshaller(marshaller);
        ExecutionContext executionContext = new ExecutionContext();
        writer.open(executionContext);
        writer.write(list.get(0).getElements());
        writer.close();
    }
}
