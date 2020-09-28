package net.n2oapp.framework.api.metadata.meta.region;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Клиентская модель региона в виде вкладок.
 */
@Getter
@Setter
public class TabsRegion extends Region {
    @JsonProperty
    private Boolean alwaysRefresh;
    @JsonProperty
    private Boolean lazy;

    @JsonProperty("tabs")
    private List<Tab> items;

    @Getter
    @Setter
    public static class Tab extends Item {
        @JsonProperty
        private String icon;
        @JsonProperty
        private Boolean opened;
    }
}
