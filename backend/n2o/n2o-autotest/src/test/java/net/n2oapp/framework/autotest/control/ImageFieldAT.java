package net.n2oapp.framework.autotest.control;

import net.n2oapp.framework.autotest.TextPosition;
import net.n2oapp.framework.autotest.api.component.page.SimplePage;
import net.n2oapp.framework.autotest.api.component.snippet.Image;
import net.n2oapp.framework.autotest.api.component.widget.FormWidget;
import net.n2oapp.framework.autotest.run.AutoTestBase;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.metadata.pack.*;
import net.n2oapp.framework.config.selective.CompileInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Автотест компонента Image с заголовком и подзаголовком
 */
public class ImageFieldAT extends AutoTestBase {

    private SimplePage page;

    @BeforeAll
    public static void beforeClass() {
        configureSelenide();
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        builder.sources(new CompileInfo("net/n2oapp/framework/autotest/control/image_field/index.page.xml"),
                new CompileInfo("net/n2oapp/framework/autotest/blank.header.xml"));

        page = open(SimplePage.class);
        page.shouldExists();
    }

    @Override
    protected void configure(N2oApplicationBuilder builder) {
        super.configure(builder);
        builder.packs(new N2oPagesPack(), new N2oHeaderPack(), new N2oWidgetsPack(), new N2oFieldSetsPack(), new N2oControlsPack());
    }

    @Test
    public void testImageField() {
        Image image = page.widget(FormWidget.class).snippet(0, Image.class);
        image.shouldExists();
        image.shouldHaveTitle("Заголовок");
        image.shouldHaveDescription("описание");
        image.shouldHaveUrl("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAlCAMAAAADS4u8AAAAnFBMVEX////ZbFrptDj////ptDj///9YuKvZbFrptDj///9YuKvZbFrptDj///9YuKvptDj////ZbFr////ptDj///9YuKvZbFrptDj///9YuKvptDj////ZbFrptDj///9YuKvptDj///9YuKvZbFrptDj///9YuKvZbFr///9YuKvptDj///9YuKvZbFrptDj///9YuKvZbFrptDj///90gGlOAAAAMHRSTlMAEBAQICAwMDAwQEBAQFBQUGBgcHCAgICAkJCQoKCgsLCwwMDAwNDQ0ODg4PDw8PDEXJ+/AAAA+UlEQVQYGc3B61aCQBSA0U/ICtGki4ZhGKmlYXI57/9uzbgwRqHF/KnV3hBl0rTxUHrL4mjeg1BaZX3gvajNIZN2D+AXph7ygxD8wuSTSrs7uCoMnxBIq40LzIvaFAiSdVPsok2XR7f8B7O8026CNsttTFB2uY0tSm4HZZvbeEO5yW0M0AaL1y6LS36TOzJQcYaKw6lIToRoT+XBysEQyZkQeCwrKwyZnEmBfXl0QU0agPLbkFoqZxLgo6zsHWqBnMo8YFxW7jGN4rUh9tCun1fKy5i/0B91cTlwY+kWoUViI0QRKymK2EFJxUaCEoiFzEPz4nWXuI/yBYSItStrEp20AAAAAElFTkSuQmCC");
        image.shouldHaveWidth(80);
        image.shouldHaveTextPosition(TextPosition.RIGHT);

        image = page.widget(FormWidget.class).snippet(1, Image.class);
        image.shouldExists();
        image.shouldHaveTitle("Заголовок");
        image.shouldHaveDescription("описание");
        image.shouldHaveUrl("https://i-novus.ru/assets/3c502870/images/logo.png");
        image.shouldHaveWidth(300);
        image.shouldHaveTextPosition(TextPosition.LEFT);
    }

}