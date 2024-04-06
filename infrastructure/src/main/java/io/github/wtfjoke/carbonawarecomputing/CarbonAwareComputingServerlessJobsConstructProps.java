package io.github.wtfjoke.carbonawarecomputing;

import software.amazon.awscdk.services.ssm.IStringParameter;
import software.amazon.awscdk.services.stepfunctions.IChainable;
import software.amazon.awscdk.services.stepfunctions.LogOptions;

public record CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															   IChainable batchJobTask,
															   LogOptions logOptions,
															   Boolean useNativeImage
) {
	public CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															IChainable batchJobTask) {
		this(apiKey, batchJobTask, null, false);
	}

	public CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															IChainable batchJobTask,
															LogOptions logOptions) {
		this(apiKey, batchJobTask, logOptions, false);
	}

	public CarbonAwareComputingServerlessJobsConstructProps(IStringParameter apiKey,
															IChainable batchJobTask,
															Boolean useNativeImage) {
		this(apiKey, batchJobTask, null, useNativeImage);
	}
}

