public class Example648 {

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            loopCounter += i * 2;
            if (i % 2 == 0) {
                loopCounter -= 1;
            }
        }
        while (j < 2) {
            whileCounter += j * 3;
            j++;
        }
        double unrelatedDouble0 = 41.90;
        int choice = 0;
        int j = 0;
        int loopCounter = 0;
        double unrelatedDouble1 = 34.79;
        String unrelatedStr3 = "str44";
        double unrelatedDouble4 = 56.80;
        int unrelatedInt2 = 111;
        int whileCounter = 0;
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
        System.out.println(val3);
        System.out.println(val2);
        System.out.println(result1);
        System.out.println(result3);
        System.out.println(result2);
    }
}
