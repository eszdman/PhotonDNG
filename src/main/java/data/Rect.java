package data;

public class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;
    /**
     * Create a new empty Rect. All coordinates are initialized to 0.
     */
    public Rect() {}

    /**
     * Create a new rectangle with the specified coordinates. Note: no range
     * checking is performed, so the caller must ensure that left <= right and
     * top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Rect(Rect sensorPix) {
        this(sensorPix.left,sensorPix.top,sensorPix.right,sensorPix.bottom);
    }
}
