package net.n2oapp.framework.api.metadata.global.view.widget.table;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.N2oAttribute;
import net.n2oapp.framework.api.metadata.N2oComponent;
import net.n2oapp.framework.api.metadata.SourceComponent;
import net.n2oapp.framework.api.metadata.global.view.page.DefaultValuesMode;
import net.n2oapp.framework.api.metadata.global.view.page.datasource.N2oStandardDatasource;


@Getter
@Setter
@N2oComponent
public class N2oTable extends N2oAbstractTable {
    @N2oAttribute
    private FilterPosition filterPosition;
    @N2oAttribute
    private SourceComponent[] filters;
    private String filtersDatasourceId;
    private N2oStandardDatasource filtersDatasource;
    @Deprecated
    private String filtersDefaultValuesQueryId;
    private Boolean searchOnChange;
    @N2oAttribute
    private ChildrenToggle children;


    public enum FilterPosition {
        top, left;

        public String getName() {
            return this.name();
        }
    }

    /**
     * Виды отображения дочерних записей таблицы
     */
    public enum ChildrenToggle {
        collapse,   // свернутый
        expand      // раскрытый
    }

    @Deprecated
    public void adapterV4() {
        super.adapterV4();
        if (getFiltersDefaultValuesQueryId() != null) {
            N2oStandardDatasource datasource = new N2oStandardDatasource();
            setFiltersDatasource(datasource);
            datasource.setQueryId(getFiltersDefaultValuesQueryId());
            datasource.setDefaultValuesMode(DefaultValuesMode.merge);
        }
    }
}
