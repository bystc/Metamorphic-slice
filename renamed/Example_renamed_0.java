public class Example179 {
    public static void main(String[] args) {
        int ch_394 = 1;
        String un_877 = "str29";
        int un_613 = 146;
        int un_102 = 105;
        int lo_449 = 0;
        for (int i_734 = 0; i_734 < 3; i_734++) {
            lo_449 += i_734 * 2;
            if (i_734 % 2 == 0) {
                lo_449 -= 1;
            }
        }
        int wh_131 = 0;
        int j_73 = 0;
        while (j_73 < 2) {
            wh_131 += j_73 * 3;
            j_73++;
        }
        int va_229 = 0, va_107 = 0, va_472 = 0;
        int te_708 = 10, te_430 = 20, te_452 = 30;
        switch (ch_394) {
            case 0:
                va_229 = te_708 * 2;
                va_107 = te_430 + 5;
                va_472 = te_452 - 3;
                break;
            case 1:
                va_229 = te_708 + te_430;
                va_107 = te_430 * te_452;
                va_472 = te_452 / te_708;
                break;
            case 2:
                va_229 = te_708 - te_430;
                va_107 = te_430 / te_452;
                va_472 = te_452 * te_708;
                break;
            default:
                va_229 = te_708;
                va_107 = te_430;
                va_472 = te_452;
                break;
        }
        int re_783 = va_229 + va_107;
        int re_693 = va_107 + va_472;
        int re_983 = va_229 + va_472;
        System.out.println(va_229);
        System.out.println(va_107);
        System.out.println(va_472);
        System.out.println(re_783);
        System.out.println(re_693);
        System.out.println(re_983);
    }
}