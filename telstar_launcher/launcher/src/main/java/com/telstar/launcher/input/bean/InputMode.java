package com.telstar.launcher.input.bean;

public class InputMode {
    private String mode;
    private boolean isInterlace;
    private int width;
    private int height;
    private int hz;

    public InputMode(String mode, boolean isInterlace, int width, int height, int hz) {
        this.mode = mode;
        this.isInterlace = isInterlace;
        this.width = width;
        this.height = height;
        this.hz = hz;
    }

    public String getMode() {
        return this.mode;
    }

    public boolean getIsInterface() {
        return this.isInterlace;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getHz() {
        return this.hz;
    }
}
