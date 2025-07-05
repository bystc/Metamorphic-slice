/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_13.java, line: 11, variable: su_905
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_13.java
*/
public class Example425 {

    public static void main(String[] args) {
        int[] ar_771 = new int[10];
        for (int i_876 = 0; i_876 < ar_771.length; i_876++) {
            ar_771[i_876] = i_876 * 2;
        }
        int su_905 = 0, su_880 = 0, su_703 = 0;
        for (int v_177 : ar_771) {
            if (v_177 % 3 == 0)
                su_905 += v_177;
            else
                su_905 -= v_177;
        }
    }
}
