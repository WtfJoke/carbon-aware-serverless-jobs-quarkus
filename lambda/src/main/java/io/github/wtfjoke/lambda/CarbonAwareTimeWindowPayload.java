package io.github.wtfjoke.lambda;

import java.time.ZonedDateTime;

import io.github.wtfjoke.lambda.carbon.aware.computing.Location;

public record CarbonAwareTimeWindowPayload(
		Location location,
		ZonedDateTime earliestDateTime,
		Long latestStartInMinutes
) {

	public CarbonAwareTimeWindowPayload(Location location, ZonedDateTime earliestDateTime) {
		this(location, earliestDateTime, (long) (24 * 60)); // 1 DAY IN MINUTES
	}
}
