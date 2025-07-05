/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_61.java, line: 19, variable: te_837
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_61.java
*/
public class Example833 {

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
        int x_939 = 1, y_38 = 2, z_902 = 3;
        int te_970 = add(x_939, y_38);
        int te_146 = mul(te_970, z_902);
        int te_787 = sub(te_146, x_939);
        int te_915 = div(te_787, y_38);
        int te_890 = add(te_915, z_902);
        int te_837 = mul(te_890, te_970);
        System.out.println(te_837);
    }
}
