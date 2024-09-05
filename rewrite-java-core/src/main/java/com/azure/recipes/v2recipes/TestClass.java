package com.azure.recipes.v2recipes;


import com.azure.core.client.traits.HttpTrait;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.ClientOptions;

public class TestClass  implements HttpTrait<TestClass> {

    @Override
    public TestClass httpClient(HttpClient httpClient) {
        return null;
    }

    @Override
    public TestClass pipeline(HttpPipeline pipeline) {
        return null;
    }

    @Override
    public TestClass addPolicy(HttpPipelinePolicy pipelinePolicy) {
        return null;
    }

    @Override
    public TestClass retryOptions(RetryOptions retryOptions) {
        return null;
    }

    @Override
    public TestClass httpLogOptions(HttpLogOptions logOptions) {
        return null;
    }

    @Override
    public TestClass clientOptions(ClientOptions clientOptions) {
        return null;
    }
}
