package io.github.wtfjoke.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.time.ZonedDateTime;

public class GetBestRenewableEnergyTimeWindow implements RequestHandler<CarbonAwareTimeWindowPayload, CarbonAwareTimeWindowResponse> {

    @Override
    public CarbonAwareTimeWindowResponse handleRequest(CarbonAwareTimeWindowPayload input, Context context) {
        System.out.println("Hello " + input.toString());
        return new CarbonAwareTimeWindowResponse(10, ZonedDateTime.now().toString());
    }
}
