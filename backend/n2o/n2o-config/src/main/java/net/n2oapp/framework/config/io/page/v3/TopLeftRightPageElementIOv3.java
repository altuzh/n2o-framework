package net.n2oapp.framework.config.io.page.v3;

import net.n2oapp.framework.api.metadata.global.view.page.N2oTopLeftRightPage;
import net.n2oapp.framework.api.metadata.global.view.region.N2oAbstractRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.config.io.widget.WidgetIOv4;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Чтение\запись страницы c тремя регионами версии 3.0
 */
@Component
public class TopLeftRightPageElementIOv3 extends BasePageElementIOv3<N2oTopLeftRightPage> {

    @Override
    public void io(Element e, N2oTopLeftRightPage m, IOProcessor p) {
        super.io(e, m, p);
        p.attributeBoolean(e, "scroll-top-button", m::getScrollTopButton, m::setScrollTopButton);
        region(e, "top", m::getTop, m::setTop, m::getTopRegionWidgets, m::setTopRegionWidgets, m.getTopOptions(), p);
        region(e, "left", m::getLeft, m::setLeft, m::getLeftRegionWidgets, m::setLeftRegionWidgets, m.getLeftOptions(), p);
        region(e, "right", m::getRight, m::setRight, m::getRightRegionWidgets, m::setRightRegionWidgets, m.getRightOptions(), p);
    }

    private void region(Element e, String name,
                        Supplier<N2oAbstractRegion[]> regionsGetter, Consumer<N2oAbstractRegion[]> regionsSetter,
                        Supplier<N2oWidget[]> widgetsGetter, Consumer<N2oWidget[]> widgetsSetter,
                        N2oTopLeftRightPage.RegionOptions regionsOptions, IOProcessor p) {
        p.anyChildren(e, name, regionsGetter, regionsSetter, p.anyOf(N2oAbstractRegion.class)
                .ignore(getWidgets()), getRegionDefaultNamespace());
        p.anyChildren(e, name, widgetsGetter, widgetsSetter, p.anyOf(N2oWidget.class)
                .ignore(getRegions()), WidgetIOv4.NAMESPACE);
        p.childAttribute(e, name, "width", regionsOptions::getWidth, regionsOptions::setWidth);
        p.childAttributeBoolean(e, name, "fixed", regionsOptions::getFixed, regionsOptions::setFixed);
        p.childAttributeInteger(e, name, "offset", regionsOptions::getOffset, regionsOptions::setOffset);
    }

    @Override
    public Class<N2oTopLeftRightPage> getElementClass() {
        return N2oTopLeftRightPage.class;
    }

    @Override
    public N2oTopLeftRightPage newInstance(Element element) {
        return new N2oTopLeftRightPage();
    }

    @Override
    public String getElementName() {
        return "top-left-right-page";
    }
}
