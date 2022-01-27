package net.n2oapp.framework.config.io.control.v2.plain;

import net.n2oapp.framework.api.metadata.control.plain.N2oAlert;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

@Component
public class AlertIOv2 extends PlainFieldIOv2<N2oAlert> {

    @Override
    public void io(Element e, N2oAlert m, IOProcessor p) {
        super.io(e, m, p);
        p.attribute(e, "title", m::getTitle, m::setTitle);
        p.attribute(e, "text", m::getText, m::setText);
        p.attribute(e, "style", m::getStyle, m::setStyle);
        p.attribute(e, "href", m::getHref, m::setHref);
        p.attribute(e, "color", m::getColor, m::setColor);
    }

    @Override
    public Class<N2oAlert> getElementClass() {
        return N2oAlert.class;
    }

    @Override
    public String getElementName() {
        return "alert";
    }
}
