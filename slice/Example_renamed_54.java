/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_54.java, line: 19, variable: te_727
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_54.java
*/
public class Example41 {

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
        int x_704 = 1, y_385 = 2, z_419 = 3;
        int te_443 = add(x_704, y_385);
        int te_868 = mul(te_443, z_419);
        int te_96 = sub(te_868, x_704);
        int te_583 = div(te_96, y_385);
        int te_682 = add(te_583, z_419);
        int te_727 = mul(te_682, te_443);
        System.out.println(te_727);
    }
}
