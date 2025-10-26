package ac.anticheat.vertex.utils.kireiko.millennium.math;

public final class FastMath {

    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float[] SIN_TABLE = new float[65536];
    private static final int[] multiplyDeBruijnBitPosition;

    static {
        int i;

        for (i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

        for (i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin(((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F));
        }

        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math.sin((float) i * 0.017453292F);
        }
    }

    public static float sin(float value) {
        return SIN_TABLE[(int) (value * 10430.378F) & 65535];
    }

    public static float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }

    public static float fastSin(float value) {
        return SIN_TABLE_FAST[(int) (value * 651.8986F) & 4095];
    }

    public static float fastCos(float value) {
        return SIN_TABLE_FAST[(int) ((value + ((float) Math.PI / 2F)) * 651.8986F) & 4095];
    }

    private static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    private static int calculateLogBaseTwoDeBruijn(int value) {
        value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int) ((long) value * 125613361L >> 27) & 31];
    }

    public static int roundUpToPowerOfTwo(int value) {
        int var1 = value - 1;
        var1 |= var1 >> 1;
        var1 |= var1 >> 2;
        var1 |= var1 >> 4;
        var1 |= var1 >> 8;
        var1 |= var1 >> 16;
        return var1 + 1;
    }

    public static int floor(double value) {
        int var2 = (int) value;
        return value < (double) var2 ? var2 - 1 : var2;
    }

    public static int calculateLogBaseTwo(int i) {
        return calculateLogBaseTwoDeBruijn(i) - (isPowerOfTwo(i) ? 0 : 1);
    }
}
