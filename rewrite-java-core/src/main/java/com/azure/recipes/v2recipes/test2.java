package com.azure.recipes.v2recipes;


import io.clientcore.core.models.traits.HttpTrait;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.ClientOptions;

public class test2 implements HttpTrait <test2>{

    @Override
    public test2 httpClient(HttpClient httpClient) {
        return null;
    }

    @Override
    public test2 pipeline(HttpPipeline pipeline) {
        return null;
    }

    @Override
    public test2 addPolicy(HttpPipelinePolicy pipelinePolicy) {
        return null;
    }

    @Override
    public test2 retryOptions(RetryOptions retryOptions) {
        return null;
    }

    @Override
    public test2 httpLogOptions(HttpLogOptions logOptions) {
        return null;
    }

    @Override
    public test2 clientOptions(ClientOptions clientOptions) {
        return null;
    }

    public test2 httpRedirectOptions(io.clientcore.core.http.models.HttpRedirectOptions redirectOptions) {
        return null;
    }
}
