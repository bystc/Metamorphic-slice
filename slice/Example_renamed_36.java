/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_36.java, line: 11, variable: su_566
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_36.java
*/
public class Example487 {

    public static void main(String[] args) {
        int[] ar_611 = new int[10];
        for (int i_784 = 0; i_784 < ar_611.length; i_784++) {
            ar_611[i_784] = i_784 * 2;
        }
        int su_566 = 0, su_163 = 0, su_354 = 0;
        for (int v_643 : ar_611) {
            if (v_643 % 3 == 0)
                su_566 += v_643;
            else
                su_566 -= v_643;
        }
    }
}
