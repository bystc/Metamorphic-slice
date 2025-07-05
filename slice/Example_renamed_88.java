/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_88.java, line: 11, variable: su_754
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_88.java
*/
public class Example10 {

    public static void main(String[] args) {
        int[] ar_776 = new int[10];
        for (int i_61 = 0; i_61 < ar_776.length; i_61++) {
            ar_776[i_61] = i_61 * 2;
        }
        int su_754 = 0, su_536 = 0, su_417 = 0;
        for (int v_124 : ar_776) {
            if (v_124 % 3 == 0)
                su_754 += v_124;
            else
                su_754 -= v_124;
        }
    }
}
