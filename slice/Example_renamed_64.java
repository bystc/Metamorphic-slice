/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_64.java, line: 19, variable: te_342
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_64.java
*/
public class Example485 {

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
        int x_189 = 1, y_327 = 2, z_249 = 3;
        int te_406 = add(x_189, y_327);
        int te_704 = mul(te_406, z_249);
        int te_142 = sub(te_704, x_189);
        int te_459 = div(te_142, y_327);
        int te_971 = add(te_459, z_249);
        int te_342 = mul(te_971, te_406);
        System.out.println(te_342);
    }
}
