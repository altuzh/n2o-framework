package net.n2oapp.framework.config.io.widget.table;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.global.view.action.LabelType;
import net.n2oapp.framework.api.metadata.global.view.widget.table.N2oTable;
import net.n2oapp.framework.api.metadata.global.view.widget.table.Size;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.*;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oCell;
import net.n2oapp.framework.api.metadata.io.ElementIOFactory;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.persister.TypedElementPersister;
import net.n2oapp.framework.api.metadata.reader.TypedElementReader;
import net.n2oapp.framework.config.io.control.ControlIOv2;
import net.n2oapp.framework.config.io.fieldset.FieldsetIOv4;
import net.n2oapp.framework.config.io.widget.AbstractListWidgetElementIOv4;
import net.n2oapp.framework.config.io.widget.table.cell.CellIOv2;
import org.jdom.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись таблицы
 */
@Component
public class TableElementIOV4 extends AbstractListWidgetElementIOv4<N2oTable> {

    @Override
    public String getElementName() {
        return "table";
    }

    @Override
    public Class<N2oTable> getElementClass() {
        return N2oTable.class;
    }

    @Override
    public void io(Element e, N2oTable t, IOProcessor p) {
        super.io(e, t, p);
        p.attributeBoolean(e, "selected", t::getSelected, t::setSelected);
        p.attributeEnum(e, "table-size", t::getTableSize, t::setTableSize, Size.class);
        p.attribute(e, "scroll-x", t::getScrollX, t::setScrollX);
        p.attribute(e, "scroll-y", t::getScrollY, t::setScrollY);
        p.anyChildren(e, "columns", t::getColumns, t::setColumns, columns(p));
        p.childAttributeEnum(e, "filters", "place", t::getFilterPosition, t::setFilterPosition, N2oTable.FilterPosition.class);
        p.childAttributeBoolean(e, "filters", "search-on-change", t::getSearchOnChange, t::setSearchOnChange);
        p.anyChildren(e, "filters", t::getFilters, t::setFilters, p.anyOf(SourceComponent.class), FieldsetIOv4.NAMESPACE, ControlIOv2.NAMESPACE);
        p.attributeEnum(e, "children", t::getChildren, t::setChildren, N2oTable.ChildrenToggle.class);
    }

    private void abstractColumn(Element e, AbstractColumn c, IOProcessor p) {
        p.attribute(e, "id", c::getId, c::setId);
        p.attribute(e, "text-field-id", c::getTextFieldId, c::setTextFieldId);
        p.attribute(e, "tooltip-field-id", c::getTooltipFieldId, c::setTooltipFieldId);
        p.attribute(e, "visible", c::getVisible, c::setVisible);
        p.attribute(e, "label", c::getLabelName, c::setLabelName);
        p.attribute(e, "icon", c::getLabelIcon, c::setLabelIcon);
        p.attributeEnum(e, "type", c::getLabelType, c::setLabelType, LabelType.class);
        p.attribute(e, "sorting-field-id", c::getSortingFieldId, c::setSortingFieldId);
        p.attributeEnum(e, "sorting-direction", c::getSortingDirection, c::setSortingDirection, DirectionType.class);
        p.attribute(e, "width", c::getWidth, c::setWidth);
        p.attributeBoolean(e, "resizable", c::getResizable, c::setResizable);
        p.attributeEnum(e, "fixed", c::getFixed, c::setFixed, ColumnFixedPosition.class);
        p.anyChildren(e, "dependencies", c::getColumnVisibilities, c::setColumnVisibilities, p.oneOf(AbstractColumn.ColumnVisibility.class)
                .add("visibility", AbstractColumn.ColumnVisibility.class, this::dependency));
    }

    private void dependency(Element e, AbstractColumn.ColumnVisibility t, IOProcessor p) {
        p.attribute(e, "ref-widget-id", t::getRefWidgetId, t::setRefWidgetId);
        p.attributeEnum(e, "ref-model", t::getRefModel, t::setRefModel, ReduxModel.class);
        p.text(e, t::getValue, t::setValue);
    }

    private ElementIOFactory<AbstractColumn, TypedElementReader<? extends AbstractColumn>, TypedElementPersister<? super AbstractColumn>> columns(IOProcessor p) {
        return p.oneOf(AbstractColumn.class)
                .add("column", N2oSimpleColumn.class, this::column)
                .add("multi-column", N2oMultiColumn.class, this::multiColumn);
    }

    private void column(Element e, N2oSimpleColumn c, IOProcessor p) {
        abstractColumn(e, c, p);
        p.anyChild(e, null, c::getCell, c::setCell, p.anyOf(N2oCell.class).ignore("dependencies"), CellIOv2.NAMESPACE);
    }

    private void multiColumn(Element e, N2oMultiColumn c, IOProcessor p) {
        p.attribute(e, "label", c::getLabelName, c::setLabelName);
        p.anyChildren(e, null, c::getChildren, c::setChildren, columns(p));
    }
}
