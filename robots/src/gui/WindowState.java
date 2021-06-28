package gui;

public class WindowState {
    private final String name;
    private final int width;
    private final int height;
    private final boolean isHide;
    private final int x;
    private final int y;

    public WindowState(String name, int width, int height,
                       boolean isHide, float x, float y) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.isHide = isHide;
        this.x = (int) x;
        this.y = (int) y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHide() {
        return isHide;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
