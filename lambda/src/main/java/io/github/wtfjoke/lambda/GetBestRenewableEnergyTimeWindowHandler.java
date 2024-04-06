package io.github.wtfjoke.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.wtfjoke.lambda.carbon.aware.computing.CarbonAwareComputingService;
import io.github.wtfjoke.lambda.carbon.aware.computing.ForecastQueryParameters;
import io.github.wtfjoke.lambda.carbon.aware.computing.ForecastResponse;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.ZonedDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class GetBestRenewableEnergyTimeWindowHandler implements RequestHandler<CarbonAwareTimeWindowPayload, CarbonAwareTimeWindowResponse> {

	@Inject
	@RestClient
	CarbonAwareComputingService carbonAwareComputingService;

	@Override
	public CarbonAwareTimeWindowResponse handleRequest(CarbonAwareTimeWindowPayload input, Context context) {
		var startDate = input.earliestDateTime();
		var latestStartDate = startDate.plusMinutes(input.latestStartInMinutes());

		var forecastQueryParameters = new ForecastQueryParameters(input.location(), startDate, latestStartDate);
		try {
			var forecastResponse = carbonAwareComputingService.getCurrentEmissionForecasts(forecastQueryParameters.location(), forecastQueryParameters.dataStartAt()
					.toString(), forecastQueryParameters.dataEndAt().toString(), forecastQueryParameters.windowSize());
			var optimalExecutionDateTime = extractOptimalTime(forecastResponse);
			long waitTimeInSeconds = getWaitTimeInSeconds(optimalExecutionDateTime);

			System.out.println("Optimal execution time: " + optimalExecutionDateTime);
			System.out.println("Waiting for " + waitTimeInSeconds + " seconds for optimal execution time.");

			return new CarbonAwareTimeWindowResponse(waitTimeInSeconds, optimalExecutionDateTime.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private long getWaitTimeInSeconds(ZonedDateTime optimalExecutionDateTime) {
		return Math.max(ZonedDateTime.now().until(optimalExecutionDateTime, SECONDS), 0);
	}

	public ZonedDateTime extractOptimalTime(ForecastResponse[] forecastResponses) {
		if (forecastResponses.length == 0) {
			throw new IllegalArgumentException("Expected at least one forecast response, got none.");
		}
		var optimalDataPoints = forecastResponses[0].optimalDataPoints();
		if (optimalDataPoints.length != 1) {
			throw new IllegalArgumentException("Expected exactly one optimal data point, got " + forecastResponses.length + " instead.");
		}
		return ZonedDateTime.parse(optimalDataPoints[0].timestamp());
	}
}
