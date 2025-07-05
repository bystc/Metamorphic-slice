/*
	This file was automatically generated as part of a slice with criterion
	file: mutated\Example_mutated_67.java, line: 11, variable: sum1
	Original file: D:\software\Metamorphic-slice\mutated\Example_mutated_67.java
*/
public class Example122 {

    public static void main(String[] args) {
        int[] arr1 = new int[10];
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = i * 2;
        }
        int sum1 = 0, sum2 = 0, sum3 = 0;
        for (int v : arr1) {
            if (v % 3 == 0)
                sum1 += v;
            else
                sum1 -= v;
        }
    }
}
