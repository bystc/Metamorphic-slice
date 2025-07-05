/*
	This file was automatically generated as part of a slice with criterion
	file: mutated\Example_mutated_64.java, line: 19, variable: temp6
	Original file: D:\software\Metamorphic-slice\mutated\Example_mutated_64.java
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
        int x = 1, y = 2, z = 3;
        int temp1 = add(x, y);
        int temp2 = mul(temp1, z);
        int temp3 = sub(temp2, x);
        int temp4 = div(temp3, y);
        int temp5 = add(temp4, z);
        int temp6 = mul(temp5, temp1);
        System.out.println(temp6);
    }
}
