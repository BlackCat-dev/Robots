package gui;

import java.util.List;

public interface HaveStorableFrames {
    List<HasState> getDataForStore();
    void restore(PositionStore store);
}
