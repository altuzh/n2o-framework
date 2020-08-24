package net.n2oapp.framework.config.io.region.v2;

import net.n2oapp.framework.api.metadata.aware.NamespaceUriAware;
import net.n2oapp.framework.api.metadata.global.view.region.N2oAbstractRegion;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.io.NamespaceIO;
import org.jdom2.Element;

/**
 * Чтение\запись абстрактного региона версии 2.0
 */
public abstract class AbstractRegionIOv2<T extends N2oAbstractRegion & NamespaceUriAware> implements NamespaceIO<T> {
    private String[] regions = {"region", "panel", "line", "tabs"};
    private String[] widgets = {"form", "table", "tree", "list", "chart", "widget", "html"};

    @Override
    public void io(Element e, T r, IOProcessor p) {
        p.attribute(e, "id", r::getId, r::setId);
        p.attribute(e, "place", r::getPlace, r::setPlace);
        p.attribute(e, "src", r::getSrc, r::setSrc);
        p.attribute(e, "class", r::getClassName, r::setClassName);
        p.attribute(e, "style", r::getStyle, r::setStyle);
        p.anyAttributes(e, r::getExtAttributes, r::setExtAttributes);
    }

    @Override
    public String getNamespaceUri() {
        return RegionIOv2.NAMESPACE.getURI();
    }

    public String[] getRegions() {
        return regions;
    }

    public String[] getWidgets() {
        return widgets;
    }
}
