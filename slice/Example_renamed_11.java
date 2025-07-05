/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_11.java, line: 19, variable: te_195
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_11.java
*/
public class Example490 {

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
        int x_44 = 1, y_938 = 2, z_25 = 3;
        int te_288 = add(x_44, y_938);
        int te_421 = mul(te_288, z_25);
        int te_142 = sub(te_421, x_44);
        int te_962 = div(te_142, y_938);
        int te_710 = add(te_962, z_25);
        int te_195 = mul(te_710, te_288);
        System.out.println(te_195);
    }
}
