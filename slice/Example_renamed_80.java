/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_80.java, line: 19, variable: te_632
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_80.java
*/
public class Example296 {

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
        int x_582 = 1, y_506 = 2, z_226 = 3;
        int te_930 = add(x_582, y_506);
        int te_24 = mul(te_930, z_226);
        int te_329 = sub(te_24, x_582);
        int te_575 = div(te_329, y_506);
        int te_393 = add(te_575, z_226);
        int te_632 = mul(te_393, te_930);
        System.out.println(te_632);
    }
}
