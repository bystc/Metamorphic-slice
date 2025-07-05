/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_52.java, line: 19, variable: te_730
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_52.java
*/
public class Example573 {

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
        int x_762 = 1, y_896 = 2, z_64 = 3;
        int te_756 = add(x_762, y_896);
        int te_557 = mul(te_756, z_64);
        int te_176 = sub(te_557, x_762);
        int te_92 = div(te_176, y_896);
        int te_77 = add(te_92, z_64);
        int te_730 = mul(te_77, te_756);
        System.out.println(te_730);
    }
}
