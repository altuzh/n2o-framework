package net.n2oapp.framework.config.io.datasource;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oDatasource;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import org.jdom2.Element;

/**
 * Чтение/запись базового источника данных
 */
public abstract class BaseDatasourceIO<T extends N2oDatasource> extends AbstractDatasourceIO<T> {

    @Override
    public void io(Element e, T ds, IOProcessor p) {
        super.io(e, ds, p);
        p.attributeInteger(e, "size", ds::getSize, ds::setSize);
        p.anyChildren(e, "dependencies", ds::getDependencies, ds::setDependencies,
                p.oneOf(N2oDatasource.Dependency.class)
                        .add("fetch", N2oDatasource.FetchDependency.class, this::fetch)
                        .add("copy", N2oDatasource.CopyDependency.class, this::copy));
    }

    private void dependency(Element e, N2oDatasource.Dependency t, IOProcessor p) {
        p.attribute(e, "on", t::getOn, t::setOn);
    }

    private void fetch(Element e, N2oDatasource.FetchDependency t, IOProcessor p) {
        dependency(e, t, p);
        p.attributeEnum(e, "model", t::getModel, t::setModel, ReduxModel.class);
    }

    private void copy(Element e, N2oDatasource.CopyDependency c, IOProcessor p) {
        dependency(e, c, p);
        p.attribute(e, "target-field-id", c::getTargetFieldId, c::setTargetFieldId);
        p.attributeEnum(e, "target-model", c::getTargetModel, c::setTargetModel, ReduxModel.class);
        p.attributeEnum(e, "source-model", c::getSourceModel, c::setSourceModel, ReduxModel.class);
        p.attribute(e, "source-field-id", c::getSourceFieldId, c::setSourceFieldId);
        p.attributeBoolean(e, "submit", c::getSubmit, c::setSubmit);
        p.attributeBoolean(e, "apply-on-init", c::getApplyOnInit, c::setApplyOnInit);
    }
}
