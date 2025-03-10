package net.n2oapp.framework.sandbox.autotest.examples;

import net.n2oapp.framework.autotest.api.component.header.AnchorMenuItem;
import net.n2oapp.framework.autotest.api.component.header.DropdownMenuItem;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"n2o.sandbox.project-id=examples_header"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HeaderAT extends SandboxAutotestBase {

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testHeaderMenu() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().brandNameShouldBe("Хедер");
        page.breadcrumb().crumb(0).shouldHaveLabel("Главная страница");

        page.header().nav().shouldHaveSize(3);

        AnchorMenuItem menu1 = page.header().nav().anchor(0);
        menu1.labelShouldHave("Первая страница");
        menu1.urlShouldHave(getBaseUrl() + "/#/mi1");
        menu1.click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Первая страница");

        AnchorMenuItem menu2 = page.header().nav().anchor(1);
        menu2.labelShouldHave("Вторая страница");
        menu2.urlShouldHave(getBaseUrl() + "/#/mi2");
        menu2.click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Вторая страница");

        DropdownMenuItem dropdown = page.header().nav().dropdown(2);
        dropdown.click();
        dropdown.labelShouldHave("Вложенное меню");
        dropdown.item(0).labelShouldHave("Третья страница");
        dropdown.item(0).urlShouldHave(getBaseUrl() + "/#/mi4");
        dropdown.item(0).click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Третья страница");

        dropdown.click();
        dropdown.labelShouldHave("Вложенное меню");
        dropdown.item(1).labelShouldHave("Четвертая страница");
        dropdown.item(1).urlShouldHave(getBaseUrl() + "/#/mi5");
        dropdown.item(1).click();
        page.breadcrumb().crumb(0).shouldHaveLabel("Четвертая страница");

        page.header().extra().shouldHaveSize(1);
        AnchorMenuItem extraLink = page.header().extra().item(0, AnchorMenuItem.class);
        extraLink.labelShouldHave("Внешняя ссылка");
        extraLink.urlShouldHave("http://example.com/");
        extraLink.click();
        page.urlShouldMatches("http://example.com/");
    }
}

