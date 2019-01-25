package cn.gl.searchengine.hash;

import org.junit.Test;

import java.math.BigInteger;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HashTest {

    int[] hash = new int[20];

    /**
     * (md5/32)%(1024*1024) = col
     * md5%32 = row
     */
    int[] hash2 = new int[1024*1024];

    public HashTest() throws NoSuchAlgorithmException {
    }

    private void setHash(int row, int col){
        int temp = 1<<col;
        hash[row] = hash[row] | temp;
        System.out.println(hash[row]);
    }

    private void getHash(int row, int col) {
        int temp = hash[row];
        temp = temp >> col;
        int result = temp & 1;
        System.out.println(result);

    }

    @Test
    public void test1(){
        setHash(1, 2);
        getHash(1,  3);
    }

    MessageDigest md5 = MessageDigest.getInstance("MD5");

    /**
     * https://blog.csdn.net/u012660464/article/details/78759296
     * [120, -101, 68, -81, 64, -102, -105, 53, -27, -90, -87, 47, 48, 23, 120, -123]
     * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/MessageDigest.html#digest(byte%5B%5D,int,int)
     * [-24, 44, -32, -111, 83, 28, 77, -80, -70, -109, 61, -88, 45, 25, 76, -61]
     * -31668456063506193371120966998319084349
     * https://www.google.com/search?ei=D2YbXLyGIcaR8wWzi6OwDw&q=java+%E8%AE%A1%E7%AE%97%E5%AD%97%E7%AC%A6%E4%B8%B2md5%E5%80%BC+&oq=java+%E8%AE%A1%E7%AE%97%E5%AD%97%E7%AC%A6%E4%B8%B2md5%E5%80%BC+&gs_l=psy-ab.3...7434891.7440602..7440937...0.0..0.170.1179.0j8......0....1..gws-wiz.......33i160j35i39j0i30j35i304i39j0i13i30.NNdADEa9KoU
     * [65, -96, -95, 48, -29, 14, -55, -99, 70, -128, -45, 93, -62, -6, -19, 1]
     * 87233856564554067050740902887853583617
     * @throws NoSuchAlgorithmException
     * @throws DigestException
     */
    @Test
    public void test2() throws NoSuchAlgorithmException, DigestException {

        String url = "https://blog.csdn.net/u012660464/article/details/78759296";
        md5.update(url.getBytes());
        byte[] digest = md5.digest();
        System.out.println(Arrays.toString(digest));
        BigInteger bigInteger = new BigInteger(digest);
        System.out.println(bigInteger);
        if (bigInteger.compareTo(new BigInteger("0")) < 0){
            System.out.println("less");
            BigInteger two128 = new BigInteger("2").pow(128);
            BigInteger positive = bigInteger.add(two128);
            System.out.println(positive);

            BigInteger two20 = new BigInteger("1024").multiply(new BigInteger("1024"));
            BigInteger row_big = positive.divide(new BigInteger("32")).mod(two20);
            int row = row_big.intValue();
            System.out.println("row: "+row);



        }

    }

    @Test
    public void test3(){
        System.out.println(Math.pow(2, 128));

    }
}
