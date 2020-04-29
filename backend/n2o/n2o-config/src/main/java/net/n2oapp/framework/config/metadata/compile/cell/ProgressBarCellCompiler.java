package net.n2oapp.framework.config.metadata.compile.cell;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.table.column.cell.N2oProgressBarCell;
import org.springframework.stereotype.Component;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Компиляция ячейки иконка
 */
@Component
public class ProgressBarCellCompiler extends AbstractCellCompiler<N2oProgressBarCell, N2oProgressBarCell> {

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oProgressBarCell.class;
    }

    @Override
    public N2oProgressBarCell compile(N2oProgressBarCell source, CompileContext<?, ?> context, CompileProcessor p) {
        N2oProgressBarCell cell = new N2oProgressBarCell();
        build(cell, source, context, p, property("n2o.api.cell.progress.bar.src"));
        cell.setStriped(source.getStriped());
        cell.setActive(source.getActive());
        cell.setSize(p.cast(source.getSize(), N2oProgressBarCell.Size.normal));
        cell.setColor(source.getColor());
        return cell;
    }
}
