package io.github.kurrycat2004.enchlib.gui.components;

public interface IMouseHandler {
    boolean onMouseScroll(int mouseX, int mouseY, int delta);

    boolean onMouseClick(int mouseX, int mouseY, int button);

    boolean onMouseRelease(int mouseX, int mouseY, int button);

    default boolean onMouseDrag(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        return false;
    }
}
