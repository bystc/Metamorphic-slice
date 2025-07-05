/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_60.java, line: 11, variable: su_325
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_60.java
*/
public class Example22 {

    public static void main(String[] args) {
        int[] ar_0 = new int[10];
        for (int i_120 = 0; i_120 < ar_0.length; i_120++) {
            ar_0[i_120] = i_120 * 2;
        }
        int su_325 = 0, su_965 = 0, su_855 = 0;
        for (int v_444 : ar_0) {
            if (v_444 % 3 == 0)
                su_325 += v_444;
            else
                su_325 -= v_444;
        }
    }
}
