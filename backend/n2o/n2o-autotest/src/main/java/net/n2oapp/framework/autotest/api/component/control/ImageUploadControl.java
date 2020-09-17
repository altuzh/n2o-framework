package net.n2oapp.framework.autotest.api.component.control;

import net.n2oapp.framework.autotest.api.component.page.Page;

import java.io.File;

/**
 * Загрузка изображения для автотестирования
 */
public interface ImageUploadControl extends Control {

    File uploadImage(File... file);

    File uploadFromClasspath(String... fileName);

    void deleteImage(int index);

    void shouldHavePreview(int index);

    void shouldNotHavePreview(int index);

    previewDialog openPreviewDialog(Page page, int index);

    void shouldHaveSize(int size);

    void nameInfoShouldExist(int index);

    void nameInfoShouldNotExist(int index);

    void nameShouldBe(int index, String fileName);

    void sizeInfoShouldBeVisible(int index);

    void sizeInfoShouldBeInvisible(int index);

    void sizeShouldBe(int index, String fileSize);

    interface previewDialog {

        void shouldExists();

        void close();

        void shouldHaveLink(String link);

    }

}