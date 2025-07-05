/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_81.java, line: 11, variable: su_319
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_81.java
*/
public class Example471 {

    public static void main(String[] args) {
        int[] ar_311 = new int[10];
        for (int i_790 = 0; i_790 < ar_311.length; i_790++) {
            ar_311[i_790] = i_790 * 2;
        }
        int su_319 = 0, su_645 = 0, su_36 = 0;
        for (int v_462 : ar_311) {
            if (v_462 % 3 == 0)
                su_319 += v_462;
            else
                su_319 -= v_462;
        }
    }
}
