package com.example.camerautil.APM.bean;
import android.hardware.Camera.Area;
import java.util.List;

public class FocusArea {
    private final List<Area> focusAreas;
    private final List<Area> meteringAreas;
    private boolean restoreFocus = false;

    private FocusArea(List<Area> focusAreas, List<Area> meteringAreas) {
        this.focusAreas = focusAreas;
        this.meteringAreas = meteringAreas;
    }

    public static FocusArea create(List<Area> focusAreas, List<Area> meteringAreas) {
        return new FocusArea(focusAreas, meteringAreas);
    }

    public List<Area> getFocusAreas() {
        return this.focusAreas;
    }

    public List<Area> getMeteringAreas() {
        return this.meteringAreas;
    }

    public void setRestoreFocusMode(boolean restore) {
        this.restoreFocus = restore;
    }

    public boolean isRestoreFocusMode() {
        return this.restoreFocus;
    }
}