public class Example230 {
    public static void main(String[] args) {
        int ch_507 = 3;
        double un_804 = 79.06;
        int[] un_683 = { 2, 2, 1 };
        double un_34 = 82.19;
        int lo_17 = 0;
        for (int i_512 = 0; i_512 < 3; i_512++) {
            lo_17 += i_512 * 2;
            if (i_512 % 2 == 0) {
                lo_17 -= 1;
            }
        }
        int wh_393 = 0;
        int j_61 = 0;
        while (j_61 < 2) {
            wh_393 += j_61 * 3;
            j_61++;
        }
        int va_734 = 0, va_27 = 0, va_731 = 0;
        int te_239 = 10, te_679 = 20, te_309 = 30;
        switch (ch_507) {
            case 0:
                va_734 = te_239 * 2;
                va_27 = te_679 + 5;
                va_731 = te_309 - 3;
                break;
            case 1:
                va_734 = te_239 + te_679;
                va_27 = te_679 * te_309;
                va_731 = te_309 / te_239;
                break;
            case 2:
                va_734 = te_239 - te_679;
                va_27 = te_679 / te_309;
                va_731 = te_309 * te_239;
                break;
            default:
                va_734 = te_239;
                va_27 = te_679;
                va_731 = te_309;
                break;
        }
        int re_553 = va_734 + va_27;
        int re_24 = va_27 + va_731;
        int re_423 = va_734 + va_731;
        System.out.println(va_734);
        System.out.println(va_27);
        System.out.println(va_731);
        System.out.println(re_553);
        System.out.println(re_24);
        System.out.println(re_423);
    }
}