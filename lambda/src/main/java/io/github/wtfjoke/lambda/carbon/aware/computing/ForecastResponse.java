package io.github.wtfjoke.lambda.carbon.aware.computing;

public record ForecastResponse(
		String location,
		Integer windowSize,
		ForecastOptimalDataPoint[] optimalDataPoints
) { }
