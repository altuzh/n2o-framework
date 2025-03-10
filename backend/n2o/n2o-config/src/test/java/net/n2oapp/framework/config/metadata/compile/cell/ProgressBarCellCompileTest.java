package net.n2oapp.framework.config.metadata.compile.cell;

import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oProgressBarCell;
import net.n2oapp.framework.api.metadata.meta.cell.ProgressBarCell;
import net.n2oapp.framework.api.metadata.meta.page.SimplePage;
import net.n2oapp.framework.api.metadata.meta.widget.table.Table;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.cell.v2.ProgressBarCellElementIOv2;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.pack.N2oPagesPack;
import net.n2oapp.framework.config.metadata.pack.N2oRegionsPack;
import net.n2oapp.framework.config.metadata.pack.N2oWidgetsPack;
import net.n2oapp.framework.config.test.SourceCompileTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест на компиляцию ячейки с индикатором прогресса
 */
public class ProgressBarCellCompileTest extends SourceCompileTestBase {
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oRegionsPack(), new N2oWidgetsPack());
        builder.ios(new ProgressBarCellElementIOv2());
        builder.compilers(new ProgressBarCellCompiler());
    }

    @Test
    public void testProgressBarCell() {
        SimplePage page = (SimplePage) compile("net/n2oapp/framework/config/metadata/compile/cell/testProgressBarCell.page.xml")
                .get(new PageContext("testProgressBarCell"));
        Table table = (Table) page.getWidget();
        ProgressBarCell cell = (ProgressBarCell) table.getComponent().getCells().get(0);
        assertThat(cell.getSrc(), is("ProgressBarCell"));
        assertThat(cell.getActive(), is(true));
        assertThat(cell.getColor(), is("info"));
        assertThat(cell.getSize(), is(N2oProgressBarCell.Size.large));
        assertThat(cell.getStriped(), is(true));

        cell = (ProgressBarCell) table.getComponent().getCells().get(1);
        assertThat(cell.getSrc(), is("ProgressBarCell"));
        assertThat(cell.getSize(), is(N2oProgressBarCell.Size.normal));
    }
}
