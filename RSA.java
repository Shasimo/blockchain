import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RSA {
    private String publicKey;
    private String privateKey;
    private int[] primeNumbers = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61, 67,
            71, 73, 79, 83, 89, 97, 101, 103,
            107, 109, 113, 127, 131, 137, 139,
            149, 151, 157, 163, 167, 173, 179,
            181, 191, 193, 197, 199, 211, 223,
            227, 229, 233, 239, 241, 251, 257,
            263, 269, 271, 277, 281, 283, 293,
            307, 311, 313, 317, 331, 337, 347, 349};

    RSA(){
        generateKeys();
        System.out.println("My public key is " + this.publicKey);
        System.out.println("My private key is "  + this.privateKey);
    }

    private BigInteger generatePrimeNumber(int size){
        return BigInteger.probablePrime(size,new Random());
    }


    private static int gcd(int e, int z)
    {
        if (e == 0)
            return z;
        else
            return gcd(z % e, e);
    }

    private void generateKeys() {

        //First we need to select two random prime numbers
        BigInteger firstPrime = generatePrimeNumber(4);
        BigInteger secondPrime = generatePrimeNumber(4);
        System.out.println("FirstPrime is " + firstPrime);
        System.out.println("SecondPrime is " + secondPrime);
        //The first part of the public key is n = P*Q where P et Q are prime numbers generated random.
        BigInteger n = firstPrime.multiply(secondPrime);

        //Second part of the public key is a small exponent e which is an integer and not a factor of n and -1<e<z where z = (P-1)*(Q-1)
        firstPrime = firstPrime.subtract(new BigInteger("1"));
        secondPrime = secondPrime.subtract(new BigInteger("1"));
        BigInteger z = firstPrime.multiply(secondPrime);
        System.out.println("z is "+ z);
        BigInteger e = new BigInteger("2");

        for (; e.compareTo(z) < 0; e=e.add(new BigInteger("1"))) {

            // e is for public key exponent
            if (e.gcd(z).equals(new BigInteger("1"))) {
                break;
            }

        }


        //The public key is made of n and e
        //private key , with d the exponent of the private key.
        BigInteger d= new BigInteger("0");
        for (BigInteger i = new BigInteger("0"); i.compareTo(new BigInteger("9")) != 0; i=i.add(new BigInteger("1"))) {
             BigInteger x = i.multiply(z);
             x=x.add(new BigInteger("1"));
             // d is for private key exponent

              if (x.mod(e).equals(new BigInteger("0"))) {
                 d = x.divide(e);
                 break;
              }
        }

        System.out.println("e is " + e);
        System.out.println("d is " + d);
        System.out.println("n is " + n);
        System.out.println("Public key is {e,n} " + e.toString() + " " +n.toString());
        System.out.println("Private key is {d,n} " + d.toString() + " "+ n.toString());



    }

}
