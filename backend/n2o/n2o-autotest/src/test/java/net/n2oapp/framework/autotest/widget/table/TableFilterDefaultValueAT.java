package net.n2oapp.framework.autotest.widget.table;

import com.codeborne.selenide.Selenide;
import net.n2oapp.framework.autotest.api.component.control.InputText;
import net.n2oapp.framework.autotest.api.component.control.RadioGroup;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.page.StandardPage;
import net.n2oapp.framework.autotest.api.component.region.SimpleRegion;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllDataPack;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oApplicationPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Автотест для проверки значений по умолчанию фильтров таблицы
 */
public class TableFilterDefaultValueAT extends AutoTestBase {
    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oApplicationPack(), new N2oAllPagesPack(), new N2oAllDataPack());
        builder.sources(
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/test.query.xml"));
    }

    @Test
    public void testSimple() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/default_value/index.page.xml"));
        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        TableWidget table = page.widget(TableWidget.class);
        InputText filter = table.filters().fields().field("name").control(InputText.class);
        TableWidget.Rows rows = table.columns().rows();

        filter.shouldHaveValue("2");
        rows.shouldHaveSize(1);
        rows.row(0).cell(1).textShouldHave("test2");

        filter.val("4");
        table.filters().toolbar().button("Найти").click();
        rows.row(0).cell(1).textShouldHave("test4");

        // filter value should saved after refresh
        Selenide.refresh();
        rows.row(0).cell(1).textShouldHave("test4");

        table.filters().toolbar().button("Сбросить").click();
        rows.shouldHaveSize(4);
        filter.shouldBeEmpty();
    }

    @Test
    public void testDefaultValuesQueryId() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/default_values_query_id/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/default.query.xml"));
        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        TableWidget table = page.widget(TableWidget.class);
        InputText filter = table.filters().fields().field("name").control(InputText.class);
        TableWidget.Rows rows = table.columns().rows();

        filter.shouldHaveValue("test3");
        rows.shouldHaveSize(1);
        rows.row(0).cell(1).textShouldHave("test3");

        filter.val("4");
        table.filters().toolbar().button("Найти").click();
        rows.row(0).cell(1).textShouldHave("test4");

        //todo NNO-7523 filter value should saved after refresh
        /*Selenide.refresh();
        rows.row(0).cell(1).textShouldHave("test4");

        table.filters().toolbar().button("Сбросить").click();
        rows.shouldHaveSize(4);
        filter.shouldBeEmpty();*/
    }

    @Test
    public void testPriority() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/priority/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/default.query.xml"));
        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        TableWidget table = page.widget(TableWidget.class);
        InputText filter = table.filters().fields().field("name").control(InputText.class);
        TableWidget.Rows rows = table.columns().rows();

        filter.shouldHaveValue("test3");
        rows.shouldHaveSize(1);
        rows.row(0).cell(1).textShouldHave("test3");

        filter.val("4");
        table.filters().toolbar().button("Найти").click();
        rows.row(0).cell(1).textShouldHave("test4");

        //todo NNO-7523 filter value should saved after refresh
        /*Selenide.refresh();
        rows.row(0).cell(1).textShouldHave("test4");

        table.filters().toolbar().button("Сбросить").click();
        rows.shouldHaveSize(4);
        filter.shouldBeEmpty();*/
    }

    @Test
    public void testFormAsFilter() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/form_as_filter/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/form_as_filter/test.query.xml"));
        StandardPage page = open(StandardPage.class);
        page.shouldExists();

        TableWidget table = page.regions().region(0, SimpleRegion.class).content().widget(1, TableWidget.class);
        TableWidget.Rows rows = table.columns().rows();
        rows.shouldHaveSize(1);

        FormWidget form = page.regions().region(0, SimpleRegion.class).content().widget(0, FormWidget.class);
        RadioGroup period = form.fields().field("Period").control(RadioGroup.class);
        period.check("Half year");
        rows.shouldHaveSize(2);

        Selenide.refresh();

        period.shouldBeChecked("Half year");
        rows.shouldHaveSize(2);

        period.check("5 years");
        rows.shouldHaveSize(1);
    }

    @Test
    public void testFiltersDatasource() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/filters_datasource/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/widget/table/filters/default.query.xml"));
        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        TableWidget table = page.widget(TableWidget.class);
        InputText filter = table.filters().fields().field("name").control(InputText.class);
        TableWidget.Rows rows = table.columns().rows();

        filter.shouldHaveValue("test3");
        rows.shouldHaveSize(1);
        rows.row(0).cell(1).textShouldHave("test3");

        filter.val("4");
        table.filters().toolbar().button("Найти").click();
        rows.row(0).cell(1).textShouldHave("test4");

        //todo NNO-7523 filter value should saved after refresh
        /*Selenide.refresh();
        rows.row(0).cell(1).textShouldHave("test4");

        table.filters().toolbar().button("Сбросить").click();
        rows.shouldHaveSize(4);
        filter.shouldBeEmpty();*/
    }
}

