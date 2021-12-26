package gui;

import java.io.Serializable;

public class WindowState implements Serializable {
    private String name;
    private int width;
    private int height;
    private int positionX;
    private int positionY;
    private boolean isMax;
    private boolean isMin;

    public WindowState(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public boolean isMax() {
        return isMax;
    }

    public void setMax(boolean max) {
        isMax = max;
    }

    public boolean isMin() {
        return isMin;
    }

    public void setMin(boolean min) {
        isMin = min;
    }
}
