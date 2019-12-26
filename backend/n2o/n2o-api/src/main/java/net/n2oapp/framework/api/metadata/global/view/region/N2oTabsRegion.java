package net.n2oapp.framework.api.metadata.global.view.region;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель региона в виде вкладок
 */
@Getter
@Setter
public class N2oTabsRegion extends N2oRegion {

    private Boolean alwaysRefresh;

    @Override
    public String getAlias() {
        return "tab";
    }
}
