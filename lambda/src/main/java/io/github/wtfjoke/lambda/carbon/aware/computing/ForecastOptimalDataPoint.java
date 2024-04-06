package io.github.wtfjoke.lambda.carbon.aware.computing;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ForecastOptimalDataPoint(
		String timestamp,
		Integer value
) { }
