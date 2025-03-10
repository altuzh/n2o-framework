package net.n2oapp.framework.autotest.api.component;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.autotest.api.component.badge.Badge;

public interface DropDown extends Component {

    DropDownItem item(int index);

    void shouldHaveItems(int size);

    interface DropDownItem extends Component, Badge {
        void shouldHaveValue(String value);
    }
}
