package io.github.wtfjoke.lambda.carbon.aware.computing;

import java.time.ZonedDateTime;
import java.util.Optional;

public record ForecastQueryParameters(Country location, ZonedDateTime dataStartAt, ZonedDateTime dataEndAt, Integer windowSize) {

	public ForecastQueryParameters(Country location, ZonedDateTime dataStartAt, ZonedDateTime dataEndAt) {
		this(location, dataStartAt, dataEndAt, null);
	}

	String asQueryParameters() {
		return "location=" + location + "&dataStartAt=" + dataStartAt + "&dataEndAt=" + dataEndAt  + Optional.ofNullable(windowSize).map(ws -> "&windowSize=" + ws).orElse("");
	}
}
