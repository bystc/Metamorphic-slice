/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_44.java, line: 11, variable: su_323
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_44.java
*/
public class Example789 {

    public static void main(String[] args) {
        int[] ar_158 = new int[10];
        for (int i_66 = 0; i_66 < ar_158.length; i_66++) {
            ar_158[i_66] = i_66 * 2;
        }
        int su_323 = 0, su_886 = 0, su_400 = 0;
        for (int v_590 : ar_158) {
            if (v_590 % 3 == 0)
                su_323 += v_590;
            else
                su_323 -= v_590;
        }
    }
}
