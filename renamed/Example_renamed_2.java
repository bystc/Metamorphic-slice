public class Example109 {
    public static void main(String[] args) {
        int ch_79 = 2;
        int un_70 = 123;
        double un_44 = 89.90;
        int[] un_853 = { 7, 8, 9 };
        int[] un_164 = { 0, 4, 3 };
        double un_446 = 96.48;
        int lo_855 = 0;
        for (int i_73 = 0; i_73 < 3; i_73++) {
            lo_855 += i_73 * 2;
            if (i_73 % 2 == 0) {
                lo_855 -= 1;
            }
        }
        int wh_977 = 0;
        int j_392 = 0;
        while (j_392 < 2) {
            wh_977 += j_392 * 3;
            j_392++;
        }
        int va_894 = 0, va_780 = 0, va_667 = 0;
        int te_524 = 10, te_869 = 20, te_381 = 30;
        switch (ch_79) {
            case 0:
                va_894 = te_524 * 2;
                va_780 = te_869 + 5;
                va_667 = te_381 - 3;
                break;
            case 1:
                va_894 = te_524 + te_869;
                va_780 = te_869 * te_381;
                va_667 = te_381 / te_524;
                break;
            case 2:
                va_894 = te_524 - te_869;
                va_780 = te_869 / te_381;
                va_667 = te_381 * te_524;
                break;
            default:
                va_894 = te_524;
                va_780 = te_869;
                va_667 = te_381;
                break;
        }
        int re_788 = va_894 + va_780;
        int re_371 = va_780 + va_667;
        int re_742 = va_894 + va_667;
        System.out.println(va_894);
        System.out.println(va_780);
        System.out.println(va_667);
        System.out.println(re_788);
        System.out.println(re_371);
        System.out.println(re_742);
    }
}