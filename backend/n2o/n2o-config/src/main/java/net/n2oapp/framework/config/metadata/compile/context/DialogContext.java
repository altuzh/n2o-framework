package net.n2oapp.framework.config.metadata.compile.context;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.global.view.page.N2oDialog;
import net.n2oapp.framework.api.metadata.meta.page.Dialog;

/**
 * Контекст компиляции диалога подтверждения действия
 */
@Getter
@Setter
public class DialogContext extends BaseCompileContext<Dialog, N2oDialog> {

    /**
     * Идентификатор родительского виджета, откуда вызывается действие
     */
    private String parentWidgetId;

    /**
     * Идентификатор виджета, который нужно обновить при успешном действии
     */
    private String refreshClientWidgetId;

    /**
     * Идентификатор объекта, в котором находится операция
     */
    private String objectId;

    public DialogContext(String route, String sourceId) {
        super(route, sourceId, N2oDialog.class, Dialog.class);
    }
}
