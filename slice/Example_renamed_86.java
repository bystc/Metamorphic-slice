/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_86.java, line: 19, variable: te_511
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_86.java
*/
public class Example213 {

    public static int add(int a, int b) {
        return a + b;
    }

    public static int mul(int a, int b) {
        return a * b;
    }

    public static int sub(int a, int b) {
        return a - b;
    }

    public static int div(int a, int b) {
        return b != 0 ? a / b : 0;
    }

    public static void main(String[] args) {
        int x_523 = 1, y_32 = 2, z_155 = 3;
        int te_209 = add(x_523, y_32);
        int te_420 = mul(te_209, z_155);
        int te_113 = sub(te_420, x_523);
        int te_589 = div(te_113, y_32);
        int te_26 = add(te_589, z_155);
        int te_511 = mul(te_26, te_209);
        System.out.println(te_511);
    }
}
