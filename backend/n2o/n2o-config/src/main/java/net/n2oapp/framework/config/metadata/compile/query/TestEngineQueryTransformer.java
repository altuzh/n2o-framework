package net.n2oapp.framework.config.metadata.compile.query;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.SourceClassAware;
import net.n2oapp.framework.api.metadata.compile.SourceProcessor;
import net.n2oapp.framework.api.metadata.compile.SourceTransformer;
import net.n2oapp.framework.api.metadata.dataprovider.N2oTestDataProvider;
import net.n2oapp.framework.api.metadata.global.dao.query.AbstractField;
import net.n2oapp.framework.api.metadata.global.dao.query.N2oQuery;
import net.n2oapp.framework.api.metadata.global.dao.query.field.QuerySimpleField;
import net.n2oapp.framework.config.register.route.RouteUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.colon;

/**
 * Трансформация query для тестового провайдера данных.
 * Генерирует тело для фильтров, <select/>, <sorting/>.
 */
@Component
public class TestEngineQueryTransformer implements SourceTransformer<N2oQuery>, SourceClassAware {
    @Override
    public N2oQuery transform(N2oQuery source, SourceProcessor p) {
        if (!isTest(source))
            return source;
        if (source.getFields() != null) {
            for (AbstractField field : source.getFields()) {
                if (Boolean.TRUE.equals(field.getIsSelected()) && field.getSelectExpression() == null)
                    field.setSelectExpression(field.getId());
                if (field instanceof QuerySimpleField)
                    transformSimpleField(((QuerySimpleField) field));
            }
        }
        if (source.getFilters() != null) {
            for (N2oQuery.Filter filter : source.getFilters()) {
                if (filter.getFilterId() == null)
                    filter.setFilterId(RouteUtil.normalizeParam(filter.getFieldId()) + "_" + filter.getType());
                if (filter.getText() == null)
                    filter.setText(filter.getFieldId() + " " + colon(filter.getType().name()) + " " + colon(filter.getFilterId()));
            }
        }
        return source;
    }

    private void transformSimpleField(QuerySimpleField field) {
        if (Boolean.TRUE.equals(field.getIsSorted()) && field.getSortingExpression() == null)
            field.setSortingExpression(field.getId() + " " + colon(field.getId() + "Direction"));
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oQuery.class;
    }

    private boolean isTest(N2oQuery source) {
        return checkTest(source.getLists())
                && checkTest(source.getUniques())
                && checkTest(source.getCounts());
    }

    private boolean checkTest(N2oQuery.Selection[] selection) {
        if (selection == null) return true;
        return Arrays.stream(selection).noneMatch(elem -> !(elem.getInvocation() instanceof N2oTestDataProvider));
    }
}
