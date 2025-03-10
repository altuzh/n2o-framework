package net.n2oapp.framework.api.metadata.global.view.region;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.N2oAttribute;
import net.n2oapp.framework.api.metadata.RegionItem;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.control.N2oComponent;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;

import java.util.List;
import java.util.Map;

/**
 * Модель региона 2.0
 */
@Getter
@Setter
public abstract class N2oRegion extends N2oComponent implements SourceMetadata, RegionItem {
    @N2oAttribute("Идентификатор")
    private String id;
    @N2oAttribute("Ширина")
    private String width;
    private String activeParam;
    private Boolean routable;
    @N2oAttribute("Элементы региона")
    private SourceComponent[] content;

    @Override
    public String getPostfix() {
        return "region";
    }

    public String getAlias() {
        return "w";
    }

    @Override
    public void collectWidgets(List<N2oWidget> result, Map<String, Integer> ids, String prefix) {
        if (content != null) {
            for (SourceComponent component : content) {
                if (component instanceof RegionItem)
                    ((RegionItem) component).collectWidgets(result, ids, getAlias());
            }
        }
    }
}
