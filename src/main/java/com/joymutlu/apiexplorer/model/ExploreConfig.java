package com.joymutlu.apiexplorer.model;


public class ExploreConfig {
    private final boolean withDeprecated;
    private final boolean withArguments;
    private final boolean withReturnValues;
    private final boolean withParentApi;
    private final boolean withObjectMethods;
    private final ApiViewType apiViewType;

    public ExploreConfig(
            boolean withDeprecated,
            boolean withArguments,
            boolean withReturnValues,
            boolean withParentApi,
            boolean withObjectMethods
    ) {
        this.withDeprecated = withDeprecated;
        this.withArguments = withArguments;
        this.withReturnValues = withReturnValues;
        this.withParentApi = withParentApi;
        this.withObjectMethods = withObjectMethods;
        apiViewType = withArguments
                ? this.withReturnValues ? ApiViewType.FULL : ApiViewType.METHOD_CALL
                : ApiViewType.METHOD_NAME;
    }

    public boolean withDeprecated() {
        return withDeprecated;
    }

    public boolean withArguments() {
        return withArguments;
    }

    public boolean withReturnValues() {
        return withReturnValues;
    }

    public boolean withParentApi() {
        return withParentApi;
    }

    public boolean withObjectMethods() {
        return withObjectMethods;
    }

    public ApiViewType getApiViewType() {
        return apiViewType;
    }

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public static class ConfigBuilder {
        private boolean withDeprecated;
        private boolean withArguments;
        private boolean withReturnValues;
        private boolean withParentApi;
        private boolean withObjectMethods;

        private ConfigBuilder() {

        }

        public ConfigBuilder withDeprecated(boolean withDeprecated) {
            this.withDeprecated = withDeprecated;
            return this;
        }

        public ConfigBuilder withArgumentsAndReturns(boolean withArguments, boolean withReturnValues) {
            this.withArguments = withArguments;
            this.withReturnValues = withReturnValues;
            return this;
        }

        public ConfigBuilder withParentApi(boolean withParentApi, boolean withObjectMethods) {
            this.withParentApi = withParentApi;
            this.withObjectMethods = withObjectMethods;
            return this;
        }

        public ExploreConfig build() {
            return new ExploreConfig(
                    withDeprecated,
                    withArguments,
                    withReturnValues,
                    withParentApi,
                    withObjectMethods
            );
        }
    }
}
