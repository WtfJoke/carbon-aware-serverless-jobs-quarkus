package io.github.wtfjoke.carbonawarecomputing;

import software.amazon.awscdk.services.ssm.IStringParameter;
import software.amazon.awscdk.services.stepfunctions.IChainable;

public record CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															   IChainable batchJobTask,
															   Boolean useNativeImage
) {
	public CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															IChainable batchJobTask) {
		this(apiKey, batchJobTask, false);
	}
}

