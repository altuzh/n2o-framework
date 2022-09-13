package net.n2oapp.framework.sandbox.autotest.alert;

import net.n2oapp.framework.autotest.api.component.button.Button;
import net.n2oapp.framework.autotest.api.component.control.InputText;
import net.n2oapp.framework.autotest.api.component.fieldset.MultiFieldSet;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.run.AutoTestApplication;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestApplication;
import net.n2oapp.framework.sandbox.autotest.SandboxAutotestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = {"n2o.sandbox.project-id=cases_7.23_alerts_handle"},
        classes = SandboxAutotestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlertAT extends SandboxAutotestBase {

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
        builder.sources(new CompileInfo("net/n2oapp/framework/config/default/default.application.xml"));
    }

    @Test
    public void test() {
        SimplePage page = open(SimplePage.class);
        page.shouldExists();

        Button send = page.widget(FormWidget.class).toolbar().bottomRight().button("Отправить уведомления");

        MultiFieldSet multiFieldSet = page.widget(FormWidget.class).fieldsets().fieldset(MultiFieldSet.class);
        multiFieldSet.clickAddButton();
        InputText text = multiFieldSet.item(0).fields().field("Текст сообщения").control(InputText.class);
        InputText position = multiFieldSet.item(0).fields().field("Позиция уведомления").control(InputText.class);

        text.val("Алерт 1");

        send.click();
        page.alerts("top").alert(0).shouldHaveText("Алерт 1");
    }

}
