package committee.nova.atom.eco.client.widegts;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/1/25 21:30
 * Version: 1.0
 */
public class ScreenRect {
    public final int width;
    public final int height;
    public int x;
    public int y;

    public ScreenRect (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains (int px, int py) {
        return px > x - 1 && px < (x + width) + 1 && py > y - 1 && py < (y + height) + 1;
    }
}
