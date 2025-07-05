/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_59.java, line: 11, variable: su_201
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_59.java
*/
public class Example67 {

    public static void main(String[] args) {
        int[] ar_636 = new int[10];
        for (int i_411 = 0; i_411 < ar_636.length; i_411++) {
            ar_636[i_411] = i_411 * 2;
        }
        int su_201 = 0, su_938 = 0, su_272 = 0;
        for (int v_467 : ar_636) {
            if (v_467 % 3 == 0)
                su_201 += v_467;
            else
                su_201 -= v_467;
        }
    }
}
