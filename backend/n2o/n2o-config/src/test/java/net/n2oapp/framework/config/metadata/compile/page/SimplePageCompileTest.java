package net.n2oapp.framework.config.metadata.compile.page;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.meta.page.Page;
import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.api.metadata.meta.widget.HtmlWidget;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.dataprovider.JavaDataProviderIOv1;
import net.n2oapp.framework.config.io.page.v3.SimplePageElementIOv3;
import net.n2oapp.framework.config.io.region.v2.CustomRegionIOv2;
import net.n2oapp.framework.config.io.toolbar.ButtonIO;
import net.n2oapp.framework.config.io.widget.v4.HtmlWidgetElementIOv4;
import net.n2oapp.framework.config.metadata.compile.context.ModalPageContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oAllPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oObjectsPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;


/**
 * Тестирование компиляции страницы с единственным виджетом
 */
public class SimplePageCompileTest extends SourceCompileTestBase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.ios(new SimplePageElementIOv3(), new CustomRegionIOv2(), new HtmlWidgetElementIOv4(),
                new ButtonIO(), new JavaDataProviderIOv1())
                .packs(new N2oObjectsPack(), new N2oAllPagesPack())
                .sources(new CompileInfo("net/n2oapp/framework/config/metadata/compile/object/utAction.object.xml"))
                .propertySources("application-test.properties");
    }

    @Test
    public void simplePage() {
        SimplePage page = (SimplePage) compile("net/n2oapp/framework/config/metadata/compile/page/testSimplePage.page.xml")
                .get(new PageContext("testSimplePage"));
        assertThat(page.getId(), is("test_route"));
        assertThat(page.getSrc(), is("SimplePage"));
        assertThat(page.getClassName(), is("testClass"));
        assertThat(page.getStyle().size(), is(2));
        assertThat(page.getPageProperty().getTitle(), is("testPage"));
        assertThat(page.getPageProperty().getHtmlTitle(), is("tab title"));
        assertThat(page.getPageProperty().getDatasource(), is("test_route_main"));
        assertThat(page.getPageProperty().getModel(), is(ReduxModel.edit));
        assertThat(page.getWidget(), notNullValue());
        assertThat(page.getWidget().getClass(), is(equalTo(HtmlWidget.class)));
        assertThat(page.getRoutes().getList().size(), is(1));
        assertThat(page.getRoutes().getList().get(0).getPath(), is("/test/route"));
        assertThat(page.getBreadcrumb().get(0).getLabel(), is("tesName"));
        assertThat(route("/test/route", Page.class), notNullValue());
    }

    @Test
    public void testCompileWithNonExistentAction() {
        try {
            compile("net/n2oapp/framework/config/metadata/compile/page/testCompileWithNonExistentAction.page.xml",
                    "net/n2oapp/framework/config/metadata/compile/object/utAction.object.xml")
                    .get(new PageContext("testCompileWithNonExistentAction"));
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Value by id = 'nonExistentOperation' not found"));
        }
    }

    /**
     * Тест настроек простой страницы при открытии в модальном окне
     */
    @Test
    public void simplePageInModal() {
        SimplePage page = (SimplePage) compile("net/n2oapp/framework/config/metadata/compile/page/testSimpleModalPage.page.xml")
                .get(new ModalPageContext("testSimpleModalPage", "/modal"));
        assertThat(page.getId(), is("modal"));
        assertThat(page.getPageProperty().getTitle(), nullValue());
        assertThat(page.getPageProperty().getModalHeaderTitle(), is("testPage"));
    }
}
