package com.joymutlu.apiexplorer.config;

import com.joymutlu.apiexplorer.model.ApiViewType;

public final class PluginConfig {
    private final boolean withDeprecated;
    private final boolean withArguments;
    private final boolean withReturnValues;
    private final boolean withParentApi;
    private final boolean withObjectMethods;
    private final boolean withNaturalSorting;
    private final ApiViewType apiViewType;

    public PluginConfig(
            boolean withDeprecated,
            boolean withArguments,
            boolean withReturnValues,
            boolean withParentApi,
            boolean withObjectMethods,
            boolean withNaturalSorting
    ) {
        this.withDeprecated = withDeprecated;
        this.withArguments = withArguments;
        this.withReturnValues = withReturnValues;
        this.withParentApi = withParentApi;
        this.withObjectMethods = withObjectMethods;
        apiViewType = withArguments
                ? this.withReturnValues ? ApiViewType.FULL : ApiViewType.METHOD_CALL
                : ApiViewType.METHOD_NAME;
        this.withNaturalSorting = withNaturalSorting;
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

    public boolean withNaturalSorting() {
        return withNaturalSorting;
    }

    public ApiViewType getApiViewType() {
        return apiViewType;
    }

    public static PluginConfig.ConfigBuilder builder() {
        return new PluginConfig.ConfigBuilder();
    }

    public SortingType getSortingType() {
        return withNaturalSorting ? SortingType.NAME_ONLY : SortingType.NAME_AND_GROUP;
    }

    public static class ConfigBuilder {
        private boolean withDeprecated;
        private boolean withArguments;
        private boolean withReturnValues;
        private boolean withParentApi;
        private boolean withObjectMethods;
        private boolean withNaturalSorting;

        private ConfigBuilder() {

        }

        public PluginConfig.ConfigBuilder withNaturalSorting(boolean withNaturalSorting) {
            this.withNaturalSorting = withNaturalSorting;
            return this;
        }

        public PluginConfig.ConfigBuilder withDeprecated(boolean withDeprecated) {
            this.withDeprecated = withDeprecated;
            return this;
        }

        public PluginConfig.ConfigBuilder withArgumentsAndReturns(boolean withArguments, boolean withReturnValues) {
            this.withArguments = withArguments;
            this.withReturnValues = withReturnValues;
            return this;
        }

        public PluginConfig.ConfigBuilder withParentApi(boolean withParentApi, boolean withObjectMethods) {
            this.withParentApi = withParentApi;
            this.withObjectMethods = withObjectMethods;
            return this;
        }

        public PluginConfig build() {
            return new PluginConfig(
                    withDeprecated,
                    withArguments,
                    withReturnValues,
                    withParentApi,
                    withObjectMethods,
                    withNaturalSorting
            );
        }
    }
}
