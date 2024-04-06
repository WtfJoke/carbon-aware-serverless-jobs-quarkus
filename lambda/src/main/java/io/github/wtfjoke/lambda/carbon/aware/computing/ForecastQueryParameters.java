package io.github.wtfjoke.lambda.carbon.aware.computing;

import java.time.ZonedDateTime;
import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ForecastQueryParameters(
		Location location,
		ZonedDateTime dataStartAt,
		ZonedDateTime dataEndAt,
		Integer windowSize
) {

	public ForecastQueryParameters(Location location, ZonedDateTime dataStartAt, ZonedDateTime dataEndAt) {
		this(location, dataStartAt, dataEndAt, null);
	}
}
