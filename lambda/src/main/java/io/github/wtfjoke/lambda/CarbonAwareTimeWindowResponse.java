package io.github.wtfjoke.lambda;

public record CarbonAwareTimeWindowResponse(long waitTimeInSecondsForOptimalExecution, String optimalExecutionDateTime){}
