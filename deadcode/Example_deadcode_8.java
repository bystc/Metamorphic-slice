public class Example465 {

    public static void main(String[] args) {
        if (false) {
            val1 = val1 + 47;
            val1 = val1 * 6 + 17;
            val1 = val1 + 50;
        }
        if (false) {
            for (int j = 0; j < 5; j++) {
                val1 = val1 + j;
            }
        }
        int choice = 3;
        String unrelatedStr0 = "str36";
        String unrelatedStr1 = "str52";
        int unrelatedInt2 = 161;
        int[] unrelatedArr3 = { 8, 8, 5 };
        int val1 = 0, val2 = 0, val3 = 0;
        int temp1 = 10, temp2 = 20, temp3 = 30;
        switch(choice) {
            case 0:
                val1 = temp1 * 2;
                val2 = temp2 + 5;
                val3 = temp3 - 3;
                break;
            case 1:
                val1 = temp1 + temp2;
                val2 = temp2 * temp3;
                val3 = temp3 / temp1;
                break;
            case 2:
                val1 = temp1 - temp2;
                val2 = temp2 / temp3;
                val3 = temp3 * temp1;
                break;
            default:
                val1 = temp1;
                val2 = temp2;
                val3 = temp3;
                break;
        }
        int result1 = val1 + val2;
        int result2 = val2 + val3;
        int result3 = val1 + val3;
        System.out.println(val1);
        System.out.println(val2);
        System.out.println(val3);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
