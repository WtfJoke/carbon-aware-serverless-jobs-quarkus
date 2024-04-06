package io.github.wtfjoke.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.wtfjoke.lambda.carbon.aware.computing.Client;
import io.github.wtfjoke.lambda.carbon.aware.computing.ForecastQueryParameters;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static java.time.temporal.ChronoUnit.SECONDS;

public class GetBestRenewableEnergyTimeWindow implements RequestHandler<CarbonAwareTimeWindowPayload, CarbonAwareTimeWindowResponse> {

	@Override
	public CarbonAwareTimeWindowResponse handleRequest(CarbonAwareTimeWindowPayload input, Context context) {
		var startDate = input.earliestDateTime();
		var latestStartDate = startDate.plusMinutes(input.latestStartInMinutes());

		var forecastQueryParameters = new ForecastQueryParameters(input.country(), startDate, latestStartDate);
		var client = new Client();
		try {
			ZonedDateTime optimalExecutionDateTime = client.extractOptimalTime(client.fetchForecast(forecastQueryParameters));
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
