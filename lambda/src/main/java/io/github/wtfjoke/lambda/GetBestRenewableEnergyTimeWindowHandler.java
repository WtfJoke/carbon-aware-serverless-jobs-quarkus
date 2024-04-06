package io.github.wtfjoke.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.wtfjoke.lambda.carbon.aware.computing.CarbonAwareComputingClient;
import io.github.wtfjoke.lambda.carbon.aware.computing.ForecastQueryParameters;
import jakarta.inject.Inject;

import java.time.ZonedDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class GetBestRenewableEnergyTimeWindowHandler implements RequestHandler<CarbonAwareTimeWindowPayload, CarbonAwareTimeWindowResponse> {

	@Inject
	CarbonAwareComputingClient carbonAwareComputingClient;

	@Override
	public CarbonAwareTimeWindowResponse handleRequest(CarbonAwareTimeWindowPayload input, Context context) {
		var startDate = input.earliestDateTime();
		var latestStartDate = startDate.plusMinutes(input.latestStartInMinutes());

		var forecastQueryParameters = new ForecastQueryParameters(input.country(), startDate, latestStartDate);
		try {
			ZonedDateTime optimalExecutionDateTime = carbonAwareComputingClient.extractOptimalTime(carbonAwareComputingClient.fetchForecast(forecastQueryParameters));
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
}
