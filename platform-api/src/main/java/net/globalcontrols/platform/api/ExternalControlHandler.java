package net.globalcontrols.platform.api;

import java.util.List;
import java.util.Map;

public interface ExternalControlHandler {
    String modId();
    List<String> getControlIds();
    Map<String, String> readControls();          // controlId → key name (GLFW short format)
    void writeControl(String controlId, int glfwCode);
    void writeControls(Map<String, Integer> controls);
}
