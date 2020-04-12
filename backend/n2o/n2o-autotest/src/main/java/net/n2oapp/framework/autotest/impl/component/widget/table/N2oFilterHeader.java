package net.n2oapp.framework.autotest.impl.component.widget.table;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import net.n2oapp.framework.autotest.api.component.control.Control;
import net.n2oapp.framework.autotest.api.component.field.StandardField;
import net.n2oapp.framework.autotest.api.component.widget.table.FilterHeader;

import static net.n2oapp.framework.autotest.N2oSelenide.component;

/**
 * Фильтруемый заголовок таблицы для автотестирования
 */
public class N2oFilterHeader extends N2oStandardTableHeader implements FilterHeader {
    @Override
    public <T extends Control> T filterControl(Class<T> componentClass) {
        return component(element(), StandardField.class).control(componentClass);
    }

    @Override
    public void openFilterDropdown() {
        element().$(".n2o-advanced-table-filter-btn .btn").click();
    }

    @Override
    public void clickSearchButton() {
        filterDropdown().$$(".n2o-advanced-table-filter-dropdown-buttons button").find(Condition.text("Искать")).click();
    }

    @Override
    public void clickResetButton() {
        filterDropdown().$$(".n2o-advanced-table-filter-dropdown-buttons button").find(Condition.text("Сбросить")).click();
    }

    private SelenideElement filterDropdown() {
        return element().$(".n2o-advanced-table-filter-dropdown").shouldBe(Condition.exist);
    }
}
