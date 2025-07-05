public class Example725 {
    public static void main(String[] args) {
        int ch_917 = 2;
        double un_720 = 89.45;
        int[] un_84 = { 8, 9, 1 };
        int[] un_214 = { 2, 0, 1 };
        double un_528 = 8.50;
        int[] un_538 = { 1, 2, 9 };
        int lo_439 = 0;
        for (int i_66 = 0; i_66 < 3; i_66++) {
            lo_439 += i_66 * 2;
            if (i_66 % 2 == 0) {
                lo_439 -= 1;
            }
        }
        int wh_589 = 0;
        int j_97 = 0;
        while (j_97 < 2) {
            wh_589 += j_97 * 3;
            j_97++;
        }
        int va_820 = 0, va_329 = 0, va_358 = 0;
        int te_539 = 10, te_80 = 20, te_619 = 30;
        switch (ch_917) {
            case 0:
                va_820 = te_539 * 2;
                va_329 = te_80 + 5;
                va_358 = te_619 - 3;
                break;
            case 1:
                va_820 = te_539 + te_80;
                va_329 = te_80 * te_619;
                va_358 = te_619 / te_539;
                break;
            case 2:
                va_820 = te_539 - te_80;
                va_329 = te_80 / te_619;
                va_358 = te_619 * te_539;
                break;
            default:
                va_820 = te_539;
                va_329 = te_80;
                va_358 = te_619;
                break;
        }
        int re_129 = va_820 + va_329;
        int re_608 = va_329 + va_358;
        int re_910 = va_820 + va_358;
        System.out.println(va_820);
        System.out.println(va_329);
        System.out.println(va_358);
        System.out.println(re_129);
        System.out.println(re_608);
        System.out.println(re_910);
    }
}