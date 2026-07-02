package net.globalcontrols.platform.api;

/** Platform implemention receives Minecraft key events and feeds them
 *  to the common KeyStateTracker via {@link #onKeyEvent}. */
@FunctionalInterface
public interface KeyInterceptor {
    void onKeyEvent(int glfwKeyCode, boolean pressed);
}
