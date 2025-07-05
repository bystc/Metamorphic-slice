/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_53.java, line: 19, variable: te_679
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_53.java
*/
public class Example510 {

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
        int x_737 = 1, y_774 = 2, z_326 = 3;
        int te_234 = add(x_737, y_774);
        int te_359 = mul(te_234, z_326);
        int te_114 = sub(te_359, x_737);
        int te_392 = div(te_114, y_774);
        int te_901 = add(te_392, z_326);
        int te_679 = mul(te_901, te_234);
        System.out.println(te_679);
    }
}
