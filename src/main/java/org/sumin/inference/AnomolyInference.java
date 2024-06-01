package org.sumin.inference;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;

import java.io.InputStream;


public class AnomolyInference {
    private SageMakerRuntimeClient client;
    private Dotenv dotenv;
    public AnomolyInference() {
        dotenv = Dotenv.load();
        client = SageMakerRuntimeClient.builder().region(Region.US_EAST_1).build();
    }
    protected String run(InputStream data) {
        InvokeEndpointRequest req = InvokeEndpointRequest.builder()
                .endpointName(dotenv.get("ENDPOINT_NAME"))
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromInputStream(data))
                .build();
        InvokeEndpointResponse res = client.invokeEndpoint(req);
//        System.out.println(res.body().asUtf8String());
        return res.body().asUtf8String();
    }
}
