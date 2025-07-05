public class Example49 {
    public static void main(String[] args) {
        int ch_1 = 1;
        int un_50 = 141;
        double un_379 = 98.41;
        int[] un_832 = { 6, 5, 5 };
        int va_695 = 0, va_187 = 0, va_633 = 0;
        int te_280 = 10, te_978 = 20, te_452 = 30;
        switch (ch_1) {
            case 0:
                va_695 = te_280 * 2;
                va_187 = te_978 + 5;
                va_633 = te_452 - 3;
                break;
            case 1:
                va_695 = te_280 + te_978;
                va_187 = te_978 * te_452;
                va_633 = te_452 / te_280;
                break;
            case 2:
                va_695 = te_280 - te_978;
                va_187 = te_978 / te_452;
                va_633 = te_452 * te_280;
                break;
            default:
                va_695 = te_280;
                va_187 = te_978;
                va_633 = te_452;
                break;
        }
        int re_846 = va_695 + va_187;
        int re_808 = va_187 + va_633;
        int re_154 = va_695 + va_633;
        System.out.println(va_695);
        System.out.println(va_187);
        System.out.println(va_633);
        System.out.println(re_846);
        System.out.println(re_808);
        System.out.println(re_154);
    }
}