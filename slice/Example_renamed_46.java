/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_46.java, line: 11, variable: su_908
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_46.java
*/
public class Example36 {

    public static void main(String[] args) {
        int[] ar_430 = new int[10];
        for (int i_910 = 0; i_910 < ar_430.length; i_910++) {
            ar_430[i_910] = i_910 * 2;
        }
        int su_908 = 0, su_77 = 0, su_155 = 0;
        for (int v_731 : ar_430) {
            if (v_731 % 3 == 0)
                su_908 += v_731;
            else
                su_908 -= v_731;
        }
    }
}
