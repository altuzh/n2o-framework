package net.n2oapp.framework.config.io.action.v2;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.action.N2oInvokeAction;
import net.n2oapp.framework.api.metadata.global.dao.N2oFormParam;
import net.n2oapp.framework.api.metadata.global.dao.N2oParam;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.meta.widget.MessagePlacement;
import net.n2oapp.framework.api.metadata.meta.widget.MessagePosition;
import net.n2oapp.framework.api.metadata.meta.widget.RequestMethod;
import org.jdom2.Element;
import org.springframework.stereotype.Component;

/**
 * Чтение/запись выполнения действия объекта версии 2.0
 */
@Component
public class InvokeActionElementIOV2 extends AbstractMetaActionElementIOV2<N2oInvokeAction> {
    @Override
    public void io(Element e, N2oInvokeAction ia, IOProcessor p) {
        super.io(e, ia, p);
        p.attribute(e, "operation-id", ia::getOperationId, ia::setOperationId);
        p.attribute(e, "object-id", ia::getObjectId, ia::setObjectId);
        p.attribute(e, "route", ia::getRoute, ia::setRoute);
        p.attributeBoolean(e, "clear-on-success", ia::getClearOnSuccess, ia::setClearOnSuccess);
        p.attributeBoolean(e, "message-on-success", ia::getMessageOnSuccess, ia::setMessageOnSuccess);
        p.attributeBoolean(e, "message-on-fail", ia::getMessageOnFail, ia::setMessageOnFail);
        p.attributeEnum(e, "message-position", ia::getMessagePosition, ia::setMessagePosition, MessagePosition.class);
        p.attributeEnum(e, "message-placement", ia::getMessagePlacement, ia::setMessagePlacement, MessagePlacement.class);
        p.attributeBoolean(e, "optimistic", ia::getOptimistic, ia::setOptimistic);
        p.attributeBoolean(e, "submit-all", ia::getSubmitAll, ia::setSubmitAll);
        p.attributeEnum(e, "method", ia::getMethod, ia::setMethod, RequestMethod.class);

        p.children(e, null, "form-param", ia::getFormParams, ia::setFormParams, N2oFormParam::new, this::formParam);
        p.children(e, null, "path-param", ia::getPathParams, ia::setPathParams, N2oParam::new, this::param);
        p.children(e, null, "header-param", ia::getHeaderParams, ia::setHeaderParams, N2oParam::new, this::param);
    }

    @Override
    public String getElementName() {
        return "invoke";
    }

    @Override
    public Class<N2oInvokeAction> getElementClass() {
        return N2oInvokeAction.class;
    }

    private void param(Element e, N2oParam param, IOProcessor p) {
        p.attribute(e, "name", param::getName, param::setName);
        p.attribute(e, "value", param::getValue, param::setValue);
        p.attribute(e, "datasource", param::getDatasourceId, param::setDatasourceId);
        p.attributeEnum(e, "model", param::getModel, param::setModel, ReduxModel.class);
    }

    private void formParam(Element e, N2oFormParam fp, IOProcessor p) {
        p.attribute(e, "id", fp::getId, fp::setId);
        if (fp.getId() == null)
            p.attribute(e, "name", fp::getName, fp::setName);
        p.attribute(e, "value", fp::getValue, fp::setValue);
        p.attribute(e, "datasource", fp::getDatasourceId, fp::setDatasourceId);
        p.attributeEnum(e, "model", fp::getModel, fp::setModel, ReduxModel.class);
    }
}
