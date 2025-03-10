package net.n2oapp.framework.autotest.api.component.badge;

import com.codeborne.selenide.Condition;
import net.n2oapp.framework.autotest.BadgePosition;
import net.n2oapp.framework.autotest.BadgeShape;
import net.n2oapp.framework.autotest.api.component.Component;

public interface Badge extends Component {
    default void badgeShouldBeExists() {
        element().$(".n2o-badge").shouldBe(Condition.exist);
    }

    default void badgeShouldHaveImage(String imageSrc) {
        element().$(".n2o-badge").$(".n2o-badge-image").shouldBe(Condition.exist);
        element().$(".n2o-badge").$(".n2o-badge-image").shouldBe(Condition.attributeMatching("src", ".*" + imageSrc));
    }

    default void badgeShouldHaveShape(BadgeShape shape) {
        element().$(".n2o-badge").shouldHave(Condition.cssClass(shape.name("n2o-badge--")));
    }

    default void badgeImageShouldHaveShape(BadgeShape shape) {
        element().$(".n2o-badge").$(".n2o-badge-image").shouldHave(Condition.cssClass(shape.name("n2o-badge-image--")));
    }

    default void badgeImageShouldHavePosition(BadgePosition position) {
        element().$(".n2o-badge").$(".n2o-badge-image").shouldHave(Condition.cssClass(position.name("n2o-badge-image--")));
    }

    default void badgeShouldHaveText(String text) {
        element().$(".n2o-badge").shouldHave(Condition.text(text));
    }
}
