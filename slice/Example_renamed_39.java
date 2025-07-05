/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_39.java, line: 11, variable: su_579
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_39.java
*/
public class Example852 {

    public static void main(String[] args) {
        int[] ar_667 = new int[10];
        for (int i_460 = 0; i_460 < ar_667.length; i_460++) {
            ar_667[i_460] = i_460 * 2;
        }
        int su_579 = 0, su_902 = 0, su_146 = 0;
        for (int v_15 : ar_667) {
            if (v_15 % 3 == 0)
                su_579 += v_15;
            else
                su_579 -= v_15;
        }
    }
}
