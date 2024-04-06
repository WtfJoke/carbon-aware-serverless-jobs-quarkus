package io.github.wtfjoke.lambda.carbon.aware.computing;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Client {
	private static final String BASE_API_URL = "https://forecast.carbon-aware-computing.com";
	private static final String API_KEY_PARAMETER_NAME = "/carbon-aware-computing/api-key";

	public ForecastResponse[] fetchForecast(ForecastQueryParameters queryParameters) throws Exception {
		var request = HttpRequest.newBuilder()
				.uri(new URI(BASE_API_URL + "/emissions/forecasts/current?" + queryParameters.asQueryParameters()))
				.header("x-api-key", getApiKey())
				.timeout(Duration.of(5, SECONDS))
				.GET()
				.build();

		try (var client = HttpClient.newHttpClient()) {
			System.out.println("Fetching forecast from: " + request.uri());
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new Exception("Failed to fetch forecast: " + response.body() + " (" + response.statusCode() + ")");
			}
			return new ObjectMapper().readValue(response.body(), ForecastResponse[].class);
		}
		catch (InterruptedException e) {
			throw new Exception("Failed to fetch forecast", e);
		}
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

	public String getApiKey() throws Exception {
		Optional<String> apiKey = getParameter(API_KEY_PARAMETER_NAME);

		if (apiKey.isEmpty()) {
			throw new Exception(
					"Missing Carbon Aware Computing API key in Parameter " + API_KEY_PARAMETER_NAME
			);
		}

		return apiKey.get();
	}


	public Optional<String> getParameter(String parameterName) {
		var request = GetParameterRequest.builder()
				.name(parameterName)
				.withDecryption(true)
				.build();

		try (var ssmClient = SsmClient.create()) {
			var response = ssmClient.getParameter(request);
			return Optional.of(response.parameter().value());
		}
		catch (ParameterNotFoundException e) {
			return Optional.empty();
		}
	}

}
