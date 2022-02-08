package committee.nova.atom.eco.utils.math;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/2/8 9:44
 * Version: 1.0
 */
public class MathUtil {
    /**
     * Restricts an integer between a min & max value
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int value, int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        if (value < min)
            value = min;
        else if (value > max)
            value = max;
        return value;
    }
}
