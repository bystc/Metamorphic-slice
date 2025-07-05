/*
	This file was automatically generated as part of a slice with criterion
	file: renamed\Example_renamed_51.java, line: 11, variable: su_552
	Original file: D:\software\Metamorphic-slice\renamed\Example_renamed_51.java
*/
public class Example754 {

    public static void main(String[] args) {
        int[] ar_849 = new int[10];
        for (int i_986 = 0; i_986 < ar_849.length; i_986++) {
            ar_849[i_986] = i_986 * 2;
        }
        int su_552 = 0, su_512 = 0, su_257 = 0;
        for (int v_148 : ar_849) {
            if (v_148 % 3 == 0)
                su_552 += v_148;
            else
                su_552 -= v_148;
        }
    }
}
