public class TestDependency {
    public static void main(String[] args) {
        long a = 10;
        long b = a + 5;
        long c = b * 2;
        long d = Math.abs(c);
        long e = Math.max(d, 100);
        long f = e - a;
        System.out.println(f);
    }
}
