package net.n2oapp.framework.config.io.action.v1;


import net.n2oapp.framework.config.io.action.OpenPageElementIOV1;
import net.n2oapp.framework.config.io.page.v3.StandardPageElementIOv3;
import net.n2oapp.framework.config.selective.ION2oMetadataTester;
import org.junit.Test;

/**
 * Тест на чтение/запись элемента <open-page>
 */
public class OpenPageElementIOV1Test {

    @Test
    public void testOpenPageElementIOV1Test() {
        ION2oMetadataTester tester = new ION2oMetadataTester();
        tester.ios(new StandardPageElementIOv3(), new OpenPageElementIOV1());
        assert tester.check("net/n2oapp/framework/config/io/action/v1/testOpenPageElementIOV1.page.xml");
    }
}