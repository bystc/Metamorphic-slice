/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_95.java, line: 11, variable: su_417
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_95.java
*/
public class Example959 {

    public static void main(String[] args) {
        int[] ar_33 = new int[10];
        for (int i_864 = 0; i_864 < ar_33.length; i_864++) {
            ar_33[i_864] = i_864 * 2;
        }
        int su_417 = 0, su_102 = 0, su_366 = 0;
        for (int v_400 : ar_33) {
            if (v_400 % 3 == 0)
                su_417 += v_400;
            else
                su_417 -= v_400;
        }
    }
}
