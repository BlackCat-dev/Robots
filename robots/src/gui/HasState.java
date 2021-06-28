package gui;

import java.util.Map;

public interface HasState {
    WindowState getState();
    void setState(Map<String, WindowState> store);
}
