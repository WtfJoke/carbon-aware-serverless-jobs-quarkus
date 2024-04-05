package io.github.wtfjoke.lambda;

import java.time.ZonedDateTime;

import io.github.wtfjoke.lambda.carbon.aware.computing.Country;

public record CarbonAwareTimeWindowPayload(Country country, ZonedDateTime earliestDateTime, Long latestStartInMinutes){

    public CarbonAwareTimeWindowPayload(Country country, ZonedDateTime earliestDateTime) {
        this(country, earliestDateTime, (long) (24 * 60)); // 1 DAY IN MINUTES
    }
}
