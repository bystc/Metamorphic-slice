/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_67.java, line: 11, variable: su_260
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_67.java
*/
public class Example122 {

    public static void main(String[] args) {
        int[] ar_990 = new int[10];
        for (int i_240 = 0; i_240 < ar_990.length; i_240++) {
            ar_990[i_240] = i_240 * 2;
        }
        int su_260 = 0, su_943 = 0, su_857 = 0;
        for (int v_493 : ar_990) {
            if (v_493 % 3 == 0)
                su_260 += v_493;
            else
                su_260 -= v_493;
        }
    }
}
