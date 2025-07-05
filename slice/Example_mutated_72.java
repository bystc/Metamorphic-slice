/*
	This file was automatically generated as part of a slice with criterion
	file: mutated\Example_mutated_72.java, line: 11, variable: sum
	Original file: D:\software\Metamorphic-slice\mutated\Example_mutated_72.java
*/
public class Example346 {

    public static void main(String[] args) {
        int[] arr = new int[10];
        int sum = 0, count = 0, avg = 0;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i * 2 + 1;
        }
        try {
            for (int i = 0; i < arr.length; i++) {
                sum += arr[i];
            }
        }
    }
}
