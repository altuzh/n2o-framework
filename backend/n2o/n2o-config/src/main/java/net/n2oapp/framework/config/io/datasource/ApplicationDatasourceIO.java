package net.n2oapp.framework.config.io.datasource;

import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oApplicationDatasource;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oDatasource;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение\запись источника данных, ссылающегося на источник из application.xml
 */
@Component
public class ApplicationDatasourceIO extends AbstractDatasourceIO<N2oApplicationDatasource> {

    @Override
    public void io(Element e, N2oApplicationDatasource ds, IOProcessor p) {
        super.io(e, ds, p);
        p.anyChildren(e, "dependencies", ds::getDependencies, ds::setDependencies,
                p.oneOf(N2oApplicationDatasource.Dependency.class)
                        .add("fetch", N2oDatasource.FetchDependency.class, this::fetch)
                        .add("copy", N2oDatasource.CopyDependency.class, this::copy));
    }

    @Override
    public Class<N2oApplicationDatasource> getElementClass() {
        return N2oApplicationDatasource.class;
    }

    @Override
    public String getElementName() {
        return "app-datasource";
    }
}
