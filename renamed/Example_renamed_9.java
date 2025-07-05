public class Example511 {
    public static void main(String[] args) {
        int ch_568 = 2;
        int un_901 = 155;
        int[] un_659 = { 5, 8, 6 };
        double un_186 = 28.91;
        int va_324 = 0, va_805 = 0, va_517 = 0;
        int te_531 = 10, te_977 = 20, te_858 = 30;
        switch (ch_568) {
            case 0:
                va_324 = te_531 * 2;
                va_805 = te_977 + 5;
                va_517 = te_858 - 3;
                break;
            case 1:
                va_324 = te_531 + te_977;
                va_805 = te_977 * te_858;
                va_517 = te_858 / te_531;
                break;
            case 2:
                va_324 = te_531 - te_977;
                va_805 = te_977 / te_858;
                va_517 = te_858 * te_531;
                break;
            default:
                va_324 = te_531;
                va_805 = te_977;
                va_517 = te_858;
                break;
        }
        int re_925 = va_324 + va_805;
        int re_384 = va_805 + va_517;
        int re_842 = va_324 + va_517;
        System.out.println(va_324);
        System.out.println(va_805);
        System.out.println(va_517);
        System.out.println(re_925);
        System.out.println(re_384);
        System.out.println(re_842);
    }
}