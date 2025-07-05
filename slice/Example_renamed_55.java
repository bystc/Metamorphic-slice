/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_55.java, line: 11, variable: su_491
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_55.java
*/
public class Example543 {

    public static void main(String[] args) {
        int[] ar_987 = new int[10];
        for (int i_381 = 0; i_381 < ar_987.length; i_381++) {
            ar_987[i_381] = i_381 * 2;
        }
        int su_491 = 0, su_849 = 0, su_126 = 0;
        for (int v_100 : ar_987) {
            if (v_100 % 3 == 0)
                su_491 += v_100;
            else
                su_491 -= v_100;
        }
    }
}
