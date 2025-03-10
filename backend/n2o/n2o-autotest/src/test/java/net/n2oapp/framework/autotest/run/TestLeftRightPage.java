package net.n2oapp.framework.autotest.run;

import net.n2oapp.framework.api.metadata.application.NavigationLayout;
import net.n2oapp.framework.autotest.api.collection.Alerts;
import net.n2oapp.framework.autotest.api.collection.Regions;
import net.n2oapp.framework.autotest.api.component.application.Footer;
import net.n2oapp.framework.autotest.api.component.application.Sidebar;
import net.n2oapp.framework.autotest.api.component.header.SimpleHeader;
import net.n2oapp.framework.autotest.api.component.page.LeftRightPage;
import net.n2oapp.framework.autotest.api.component.snippet.Alert;
import net.n2oapp.framework.autotest.impl.component.N2oComponent;

import static net.n2oapp.framework.autotest.N2oSelenide.collection;

public class TestLeftRightPage extends N2oComponent implements LeftRightPage {

    @Override
    public Regions left() {
        return collection(element().$(".left").$$(".region"), Regions.class);
    }

    @Override
    public Regions right() {
        return collection(element().$(".right").$$(".region"), Regions.class);
    }

    @Override
    public SimpleHeader header() {
        return null;
    }

    @Override
    public Sidebar sidebar() {
        return null;
    }

    @Override
    public Footer footer() {
        return null;
    }

    @Override
    public PageToolbar toolbar() {
        return null;
    }

    @Override
    public Breadcrumb breadcrumb() {
        return null;
    }

    @Override
    public Dialog dialog(String title) {
        return null;
    }

    @Override
    public Popover popover(String title) {
        return null;
    }

    @Override
    public Alerts alerts() {
        return null;
    }

    @Override
    public Alerts alerts(Alert.Placement placement) {
        return null;
    }

    @Override
    public void urlShouldMatches(String regexp) {

    }

    @Override
    public void titleShouldHaveText(String title) {

    }

    @Override
    public void scrollUp() {

    }

    @Override
    public void scrollDown() {

    }

    @Override
    public void shouldHaveStyle(String style) {

    }

    @Override
    public void shouldHaveLayout(NavigationLayout layout) {

    }

    @Override
    public void shouldHaveError(int statusCode) {

    }
}
