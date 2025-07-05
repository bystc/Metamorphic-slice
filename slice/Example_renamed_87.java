/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_87.java, line: 19, variable: te_209
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_87.java
*/
public class Example143 {

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
        int x_433 = 1, y_66 = 2, z_379 = 3;
        int te_532 = add(x_433, y_66);
        int te_990 = mul(te_532, z_379);
        int te_652 = sub(te_990, x_433);
        int te_198 = div(te_652, y_66);
        int te_840 = add(te_198, z_379);
        int te_209 = mul(te_840, te_532);
        System.out.println(te_209);
    }
}
