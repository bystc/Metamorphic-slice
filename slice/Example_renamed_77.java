/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_77.java, line: 11, variable: su_1
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_77.java
*/
public class Example507 {

    public static void main(String[] args) {
        int[] ar_317 = new int[10];
        for (int i_769 = 0; i_769 < ar_317.length; i_769++) {
            ar_317[i_769] = i_769 * 2;
        }
        int su_1 = 0, su_895 = 0, su_307 = 0;
        for (int v_318 : ar_317) {
            if (v_318 % 3 == 0)
                su_1 += v_318;
            else
                su_1 -= v_318;
        }
    }
}
