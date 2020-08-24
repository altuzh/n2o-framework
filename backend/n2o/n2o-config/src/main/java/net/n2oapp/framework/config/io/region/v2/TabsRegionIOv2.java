package net.n2oapp.framework.config.io.region.v2;

import net.n2oapp.framework.api.metadata.global.view.region.N2oAbstractRegion;
import net.n2oapp.framework.api.metadata.global.view.region.N2oTabsRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.widget.WidgetIOv4;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись региона в виде вкладок версии 2.0
 */
@Component
public class TabsRegionIOv2 extends AbstractRegionIOv2<N2oTabsRegion> {

    @Override
    public void io(Element e, N2oTabsRegion r, IOProcessor p) {
        super.io(e, r, p);
        p.attributeBoolean(e, "always-refresh", r::getAlwaysRefresh, r::setAlwaysRefresh);
        p.attributeBoolean(e, "lazy", r::getLazy, r::setLazy);
        p.attribute(e, "active-param", r::getActiveParam, r::setActiveParam);
        p.attributeBoolean(e, "routable", r::getRoutable, r::setRoutable);
        p.children(e, null, "tab", r::getTabs, r::setTabs, N2oTabsRegion.Tab::new, this::tabs);
        p.anyAttributes(e, r::getExtAttributes, r::setExtAttributes);
    }

    private void tabs(Element e, N2oTabsRegion.Tab t, IOProcessor p) {
        p.anyChildren(e, null, t::getWidgets, t::setWidgets, p.anyOf(N2oWidget.class)
                .ignore(getRegions()), WidgetIOv4.NAMESPACE);
        p.anyChildren(e, null, t::getRegions, t::setRegions, p.anyOf(N2oAbstractRegion.class)
                .ignore(getWidgets()), RegionIOv2.NAMESPACE);
    }

    @Override
    public String getElementName() {
        return "tabs";
    }

    @Override
    public Class<N2oTabsRegion> getElementClass() {
        return N2oTabsRegion.class;
    }
}
