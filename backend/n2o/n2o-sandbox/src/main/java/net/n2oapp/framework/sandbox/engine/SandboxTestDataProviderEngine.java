package net.n2oapp.framework.sandbox.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.dataprovider.N2oTestDataProvider;
import net.n2oapp.framework.engine.data.json.TestDataProviderEngine;
import net.n2oapp.framework.sandbox.client.SandboxRestClient;
import net.n2oapp.framework.sandbox.client.model.FileModel;
import net.n2oapp.framework.sandbox.engine.thread_local.ThreadLocalProjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Тестовый провайдер данных для чтения/изменения json при работе с sandbox
 */
@Component
public class SandboxTestDataProviderEngine extends TestDataProviderEngine {

    @Autowired
    private HttpSession session;
    @Autowired
    private SandboxRestClient restClient;

    @Override
    public Object invoke(N2oTestDataProvider invocation, Map<String, Object> inParams) {
        return super.invoke(invocation, inParams);
    }

    @Override
    protected void updateFile(String filename) {
        String projectId = ThreadLocalProjectId.getProjectId();
        try {
            String mapAsJson = super.getObjectMapper().writeValueAsString(getRepositoryData(filename));
            FileModel fileModel = new FileModel();
            fileModel.setFile(filename);
            fileModel.setSource(mapAsJson);
            restClient.putFiles(projectId, Collections.singletonList(fileModel), session);
        } catch (JsonProcessingException e) {
            throw new N2oException(e);
        }
    }

    @Override
    protected InputStream getResourceInputStream(N2oTestDataProvider invocation) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(invocation.getFile());
        if (classPathResource.exists()) {
            return classPathResource.getInputStream();
        }
        String projectId = ThreadLocalProjectId.getProjectId();
        return new ByteArrayInputStream(restClient.getFile(projectId, invocation.getFile(), session).getBytes());

    }

    @Override
    protected String richKey(String key) {
        String projectId = ThreadLocalProjectId.getProjectId();
        return projectId + "/" + key;
    }
}
