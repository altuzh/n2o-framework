package net.n2oapp.framework.sandbox.autotest.attributes;

import lombok.SneakyThrows;
import net.n2oapp.framework.access.metadata.pack.AccessSchemaPack;
import net.n2oapp.framework.autotest.api.collection.Toolbar;
import net.n2oapp.framework.autotest.api.component.button.StandardButton;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(properties = {
        "n2o.engine.test.classpath=/access/attributes/buttons/",
        "n2o.sandbox.project-id=access_attributes_buttons"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ButtonsAT extends SandboxAutotestBase {

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
        builder.packs(new AccessSchemaPack());
        CompileInfo.setSourceTypes(builder.getEnvironment().getSourceTypeRegister());
        super.configure(builder);
        builder.sources(
                new CompileInfo("META-INF/conf/default.access.xml"));
    }

    @Test
    public void testAdminAccess() {
        setUserInfo(loadUser());

        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().brandNameShouldBe("N2O");
        page.widget(FormWidget.class).shouldExists();
        page.breadcrumb().crumb(0).shouldHaveLabel("Доступ к кнопкам по sec атрибутам");

        Toolbar topLeft = page.widget(FormWidget.class).toolbar().topLeft();
        topLeft.shouldHaveSize(3);

        StandardButton button = topLeft.button(0, StandardButton.class);
        button.shouldExists();
        button.shouldHaveLabel("Доступно всем");
        button.shouldBeEnabled();

        button = topLeft.button(1, StandardButton.class);
        button.shouldExists();
        button.shouldHaveLabel("Только с ролью admin");
        button.shouldBeEnabled();

        button = topLeft.button(2, StandardButton.class);
        button.shouldExists();
        button.shouldHaveLabel("Только с правом edit");
        button.shouldBeEnabled();
    }

    @Test
    public void testAnonymousAccess() {
        setUserInfo(null);

        SimplePage page = open(SimplePage.class);
        page.shouldExists();
        page.header().brandNameShouldBe("N2O");
        page.widget(FormWidget.class).shouldExists();
        page.breadcrumb().crumb(0).shouldHaveLabel("Доступ к кнопкам по sec атрибутам");

        Toolbar topLeft = page.widget(FormWidget.class).toolbar().topLeft();
        topLeft.shouldHaveSize(2);

        StandardButton button = topLeft.button(0, StandardButton.class);
        button.shouldExists();
        button.shouldHaveLabel("Доступно всем");
        button.shouldBeEnabled();

        button = topLeft.button(1, StandardButton.class);
        button.shouldExists();
        button.shouldHaveLabel("Только анонимам");
        button.shouldBeEnabled();
    }

    @SneakyThrows
    private Map<String, Object> loadUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "Admin");
        user.put("roles", Collections.singletonList("admin"));
        user.put("permissions", Collections.singletonList("edit"));
        return user;
    }

}
