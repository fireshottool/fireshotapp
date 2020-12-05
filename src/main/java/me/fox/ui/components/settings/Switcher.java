package me.fox.ui.components.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Switcher<L, R> {

    @Setter
    private L left;
    @Setter
    private R right;

    private Object current;

    public Switcher(L left, R right) {
        this.left = left;
        this.right = right;

        this.current = left;
    }

    public boolean isLeft() {
        return this.current.equals(this.left);
    }

    public boolean isLeft(Object o) {
        return this.left.equals(o);
    }

    public boolean isRight() {
        return this.current.equals(this.right);
    }

    public boolean isRight(Object o) {
        return this.right.equals(o);
    }

    public boolean isEither(Object v) {
        return this.isLeft(v) || this.isRight(v);
    }

    public void switchSides() {
        if (this.current.equals(this.left)) {
            this.current = this.right;
            return;
        }

        this.current = this.left;
    }
}
