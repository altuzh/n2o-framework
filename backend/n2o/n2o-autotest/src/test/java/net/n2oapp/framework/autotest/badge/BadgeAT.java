package net.n2oapp.framework.autotest.badge;

import net.n2oapp.framework.autotest.BadgePosition;
import net.n2oapp.framework.autotest.BadgeShape;
import net.n2oapp.framework.autotest.api.component.DropDown;
import net.n2oapp.framework.autotest.api.component.DropDownTree;
import net.n2oapp.framework.autotest.api.component.button.StandardButton;
import net.n2oapp.framework.autotest.api.component.cell.BadgeCell;
import net.n2oapp.framework.autotest.api.component.control.InputSelect;
import net.n2oapp.framework.autotest.api.component.control.InputSelectTree;
import net.n2oapp.framework.autotest.api.component.control.Select;
import net.n2oapp.framework.autotest.api.component.header.AnchorMenuItem;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.api.component.widget.TreeWidget;
import net.n2oapp.framework.autotest.api.component.widget.table.TableWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.N2oAllPack;
import net.n2oapp.framework.config.metadata.pack.N2oApplicationPack;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Автотест для проверки работы баджей с картинками
 */

public class BadgeAT extends AutoTestBase {
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
        builder.packs(new N2oApplicationPack(), new N2oAllPack());
    }

    @Test
    public void testSelects() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/blank.application.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/selectors/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/selectors/test.query.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/selectors/tree.query.xml"));

        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        FormWidget formWidget = page.widget(FormWidget.class);
        InputSelect inputSelect = formWidget.fields().field("Ввод с выпадающим списком").control(InputSelect.class);
        inputSelect.openPopup();
        inputSelect.shouldBeOpened();
        DropDown dropDownInputSelect = inputSelect.dropdown();
        dropDownInputSelect.shouldExists();
        dropDownInputSelect.shouldHaveItems(4);
        DropDown.DropDownItem itemDropDownInputSelect = dropDownInputSelect.item(0);
        itemDropDownInputSelect.badgeShouldBeExists();
        itemDropDownInputSelect.badgeShouldHaveText("Проект 1");
        itemDropDownInputSelect.badgeShouldHaveShape(BadgeShape.SQUARE.getShape());
        itemDropDownInputSelect.badgeShouldHaveImage("hamburg-3846525__340.jpg");
        itemDropDownInputSelect.badgeImageShouldBeShape(BadgeShape.CIRCLE.getShape());
        itemDropDownInputSelect.badgeImageShouldBePosition(BadgePosition.LEFT.getPosition());

        Select select = formWidget.fields().field("Выпадающий список").control(Select.class);
        select.openPopup();
        select.shouldBeOpened();
        DropDown dropDownSelect = select.dropdown();
        dropDownSelect.shouldExists();
        dropDownSelect.shouldHaveItems(4);
        DropDown.DropDownItem itemDropDownSelect = dropDownSelect.item(0);
        itemDropDownSelect.badgeShouldBeExists();
        itemDropDownSelect.badgeShouldHaveText("Проект 1");
        itemDropDownSelect.badgeShouldHaveShape(BadgeShape.ROUNDED.getShape());
        itemDropDownSelect.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        itemDropDownSelect.badgeImageShouldBeShape(BadgeShape.ROUNDED.getShape());
        itemDropDownSelect.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());

        InputSelectTree selectTree = formWidget.fields().field("Дерево").control(InputSelectTree.class);
        selectTree.openPopup();
        selectTree.shouldBeOpened();
        DropDownTree dropDownTreeSelect = selectTree.dropdowntree();
        DropDownTree.DropDownTreeItem firstItemTree = dropDownTreeSelect.item(0);
        firstItemTree.expand();
        firstItemTree.shouldBeExpanded();
        dropDownTreeSelect.shouldHaveItems(4);
        firstItemTree.shouldHaveOption("11");
        DropDownTree.DropDownTreeItem thirdItemTree = dropDownTreeSelect.item(1);
        thirdItemTree.expand();
        thirdItemTree.shouldBeExpanded();
        dropDownTreeSelect.shouldHaveItems(5);
        DropDownTree.DropDownTreeItem fourthTreeItem = dropDownTreeSelect.item(2);
        fourthTreeItem.badgeShouldHaveText("Проект 1.1");
        fourthTreeItem.badgeShouldBeExists();
        fourthTreeItem.badgeShouldHaveShape(BadgeShape.CIRCLE.getShape());
        fourthTreeItem.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        fourthTreeItem.badgeImageShouldBeShape(BadgeShape.SQUARE.getShape());
        fourthTreeItem.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());
    }

    @Test
    public void testCell() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/blank.application.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/cell/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/cell/test.query.xml"));

        SimplePage simplePage = open(SimplePage.class);
        simplePage.shouldExists();

        TableWidget.Rows rows = simplePage.widget(TableWidget.class).columns().rows();
        rows.shouldHaveSize(3);
        int col = 3;
        BadgeCell cell = rows.row(0).cell(col, BadgeCell.class);
        cell.badgeShouldBeExists();
        cell.badgeShouldHaveShape(BadgeShape.ROUNDED.getShape());
        cell.badgeShouldHaveText("Проект 1");
        cell.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        cell.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());
        cell.badgeImageShouldBeShape(BadgeShape.ROUNDED.getShape());
    }

    @Test
    public void testButton() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/blank.application.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/button/index.page.xml"));

        SimplePage simplePage = open(SimplePage.class);
        simplePage.shouldExists();

        StandardButton firstBtn = simplePage.toolbar().topLeft().button("Первая");
        firstBtn.shouldBeEnabled();
        firstBtn.badgeShouldBeExists();
        firstBtn.badgeShouldHaveShape(BadgeShape.CIRCLE.getShape());
        firstBtn.badgeShouldHaveText("new");
        firstBtn.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        firstBtn.badgeImageShouldBeShape(BadgeShape.CIRCLE.getShape());
        firstBtn.badgeImageShouldBePosition(BadgePosition.LEFT.getPosition());

        StandardButton secondBtn = simplePage.toolbar().topLeft().button("Вторая");
        firstBtn.shouldBeEnabled();
        secondBtn.badgeShouldBeExists();
        secondBtn.badgeShouldHaveShape(BadgeShape.ROUNDED.getShape());
        secondBtn.badgeShouldHaveText("new");
        secondBtn.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        secondBtn.badgeImageShouldBeShape(BadgeShape.ROUNDED.getShape());
        secondBtn.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());

        StandardButton thirdBtn = simplePage.toolbar().topLeft().button("Третья");
        firstBtn.shouldBeEnabled();
        thirdBtn.badgeShouldBeExists();
        thirdBtn.badgeShouldHaveShape(BadgeShape.SQUARE.getShape());
        thirdBtn.badgeShouldHaveText("new");
        thirdBtn.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        thirdBtn.badgeImageShouldBeShape(BadgeShape.SQUARE.getShape());
        thirdBtn.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());
    }

    @Test
    public void testTree() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/blank.application.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/tree/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/tree/test.query.xml"));

        SimplePage simplePage = open(SimplePage.class);
        simplePage.shouldExists();

        TreeWidget treeWidget = simplePage.widget(TreeWidget.class);
        treeWidget.shouldExists();

        treeWidget.shouldHaveItems(2);
        TreeWidget.TreeItem firstItemTree = treeWidget.item(0);
        firstItemTree.expand();
        treeWidget.shouldHaveItems(4);
        firstItemTree.shouldHaveItem("11");

        TreeWidget.TreeItem secondItemTree = treeWidget.item(1);
        secondItemTree.badgeShouldBeExists();
        secondItemTree.badgeShouldHaveShape(BadgeShape.ROUNDED.getShape());
        secondItemTree.badgeShouldHaveText("Проект 1.1");
        secondItemTree.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        secondItemTree.badgeImageShouldBePosition(BadgePosition.LEFT.getPosition());
        secondItemTree.badgeImageShouldBeShape(BadgeShape.SQUARE.getShape());
    }

    @Test
    public void testMenu() {
        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/badge/menu/app.application.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/menu/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/badge/menu/test.page.xml"));
        builder.properties("n2o.application.id=app");

        SimplePage simplePage = open(SimplePage.class);
        simplePage.shouldExists();

        simplePage.breadcrumb().titleShouldHaveText("Меню с баджем");
        simplePage.header().brandNameShouldBe("Хедер");
        simplePage.header().nav().shouldHaveSize(1);

        AnchorMenuItem menuItem = simplePage.header().nav().anchor(0);
        menuItem.shouldHaveIcon();
        menuItem.iconShouldHaveCssClass("fa fa-bell");
        menuItem.badgeShouldBeExists();
        menuItem.badgeShouldHaveShape(BadgeShape.SQUARE.getShape());
        menuItem.badgeShouldHaveImage("static/hamburg-3846525__340.jpg");
        menuItem.badgeImageShouldBeShape(BadgeShape.SQUARE.getShape());
        menuItem.badgeImageShouldBePosition(BadgePosition.RIGHT.getPosition());
    }
}
