/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_57.java, line: 19, variable: te_347
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_57.java
*/
public class Example994 {

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
        int x_773 = 1, y_867 = 2, z_314 = 3;
        int te_626 = add(x_773, y_867);
        int te_918 = mul(te_626, z_314);
        int te_40 = sub(te_918, x_773);
        int te_212 = div(te_40, y_867);
        int te_471 = add(te_212, z_314);
        int te_347 = mul(te_471, te_626);
        System.out.println(te_347);
    }
}
