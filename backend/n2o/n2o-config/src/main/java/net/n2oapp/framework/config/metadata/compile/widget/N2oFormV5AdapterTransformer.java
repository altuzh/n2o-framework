package net.n2oapp.framework.config.metadata.compile.widget;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.compile.SourceTransformer;
import net.n2oapp.framework.api.metadata.global.view.page.N2oDatasource;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oForm;
import net.n2oapp.framework.config.metadata.compile.page.PageScope;
import org.springframework.stereotype.Component;

/**
 * Трансформация формы
 * Перенос старых(widget-4.0) атрибутов в новые(widget-5.0)
 */
@Component
public class N2oFormV5AdapterTransformer implements SourceTransformer<N2oForm>, SourceClassAware {

    @Override
    public N2oForm transform(N2oForm source, SourceProcessor p) {
        if (source.getSubmit() != null) {
            if (source.getDatasource() == null)
                source.setDatasource(new N2oDatasource());
            source.getDatasource().setSubmit(source.getSubmit());
            PageScope pageScope = p.getScope(PageScope.class);
            if (pageScope == null || pageScope.getWidgetIdSourceDatasourceMap() == null)
                throw new N2oException(String.format("There is a link to %s, but the context is not defined ", source.getDependsOn()));
            if (source.getDatasource().getSubmit().getRefreshWidgetId() != null) {
                source.getDatasource().getSubmit().setRefreshDatasources(
                        new String[]{pageScope.getWidgetIdSourceDatasourceMap()
                                .get(source.getDatasource().getSubmit().getRefreshWidgetId())});
            } else {
                source.getDatasource().getSubmit().setRefreshDatasources(
                        new String[]{pageScope.getWidgetIdSourceDatasourceMap()
                                .get(source.getId())});
            }
            source.getDatasource().getSubmit().setMessageWidgetId(source.getId());
        }
        return source;
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oForm.class;
    }

}
