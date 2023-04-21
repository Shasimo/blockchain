package org.blockchainElo.security.signature;


import org.blockchainElo.util.Pair;

import java.math.BigInteger;
import java.util.Random;


public class RSA {
    private String e;
    private String n;
    private String d;

    private String encryptedMessage;
    private Pair<String,String> privateKey;
    private Pair<String,String> publicKey;

    private final int[] first_primes_list = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61, 67,
            71, 73, 79, 83, 89, 97, 101, 103,
            107, 109, 113, 127, 131, 137, 139,
            149, 151, 157, 163, 167, 173, 179,
            181, 191, 193, 197, 199, 211, 223,
            227, 229, 233, 239, 241, 251, 257,
            263, 269, 271, 277, 281, 283, 293,
            307, 311, 313, 317, 331, 337, 347, 349};

    public RSA(int size){
        generateKeys(size);
//        Encrypt("message");
//        Decrypt();

    }


    private BigInteger nBitRandom(int n)
    {
        // Returns a random number
        // between 2**(n-1)+1 and 2**n-1'''
        BigInteger number = new BigInteger(n,new Random());
        BigInteger Max = new BigInteger("2").pow(n);
        Max= Max.subtract(new BigInteger("1"));
        BigInteger Min = new BigInteger("2").pow(n-1);
        Min = Min.add(new BigInteger("1"));
        while(number.compareTo(Max) > 0 || number.compareTo(Min) < 0){
            number = new BigInteger(n,new Random());
        }
        return number;
    }
    private BigInteger getLowLevelPrime(int size) {

        // Generate a prime candidate divisible
        // by first primes

        //  Repeat until a number satisfying
        //  the test isn't found
        while (true) {
            //  Obtain a random number
            BigInteger prime_candidate = nBitRandom(size);

            for (int divisor : first_primes_list)
            {
                BigInteger newDivisor = new BigInteger(Integer.toString(divisor));
                BigInteger newDivisorPowered = newDivisor.multiply(newDivisor);
                if (prime_candidate.mod(newDivisor).equals(new BigInteger("0"))
                        && newDivisorPowered.compareTo(prime_candidate) <= 0)
                    break;
                    //  If no divisor found, return value
                else
                    return prime_candidate;
            }
        }
    }

    static String expmod(BigInteger base, BigInteger exp, BigInteger mod ){
        if (exp.compareTo(new BigInteger("0"))==0) return "1";
        BigInteger expMod2 = exp.mod(new BigInteger("2"));
        if (expMod2.compareTo(new BigInteger("0"))==0){
            BigInteger returnValue = new BigInteger(expmod(base,exp.divide(new BigInteger("2")),mod));
            returnValue = returnValue.pow(2);
            returnValue = returnValue.mod(mod);
            return returnValue.toString();
            //return (int)Math.pow( expmod( base, (exp / 2), mod), 2) % mod;
        }
        else {
            BigInteger returnValue = new BigInteger(expmod(base,exp.subtract(new BigInteger("1")),mod));
            returnValue=returnValue.multiply(base);
            returnValue=returnValue.mod(mod);
            return returnValue.toString();
            //return (base * expmod( base, (exp - 1), mod)) % mod;
        }
    }

    static boolean trialComposite(BigInteger round_tester, BigInteger evenComponent,
                                  BigInteger miller_rabin_candidate, int maxDivisionsByTwo)
    {
        BigInteger expmod = new BigInteger(expmod(round_tester,evenComponent,miller_rabin_candidate));
        if (expmod.compareTo(new BigInteger("1")) == 0 )
            return false;
        for (int i = 0; i < maxDivisionsByTwo; i++)
        {
            int go = 1<<i;
            String go2=Integer.toString(go);
            BigInteger Go3 = new BigInteger(go2);
            BigInteger newExpMod=new BigInteger(expmod(round_tester,evenComponent.multiply(Go3),miller_rabin_candidate));
            BigInteger newMiller = miller_rabin_candidate.subtract(new BigInteger("1"));
            if (newExpMod.compareTo(newMiller) == 0)
                return false;
        }
        return true;
    }

    static boolean isMillerRabinPassed(BigInteger miller_rabin_candidate)
    {
        // Run 20 iterations of Rabin Miller Primality test

        int maxDivisionsByTwo = 0;
        BigInteger evenComponent = miller_rabin_candidate.subtract(new BigInteger("1"));

        while (evenComponent.mod(new BigInteger("2")).equals(new BigInteger("0")))
        {
            //evenComponent >>= 1;
            evenComponent=evenComponent.shiftRight(1);
            maxDivisionsByTwo += 1;
        }

        // Set number of trials here
        int numberOfRabinTrials = 20;
        for (int i = 0; i < (numberOfRabinTrials) ; i++)
        {
            //int round_tester = ThreadLocalRandom.current().nextInt(2, miller_rabin_candidate + 1);
            BigInteger roundTester = new BigInteger(miller_rabin_candidate.toString().length()+1,new Random());
            while (roundTester.compareTo(new BigInteger("2")) < 0){
                roundTester = new BigInteger(miller_rabin_candidate.toString().length()+1,new Random());
            }
            if (trialComposite(roundTester, evenComponent,
                    miller_rabin_candidate, maxDivisionsByTwo))
                return false;
        }
        return true;
    }

    private BigInteger generatePrime(int size){
        while(true){
            BigInteger probablePrime = getLowLevelPrime(size);
            if(isMillerRabinPassed(probablePrime)) return probablePrime;
        }

    }
    private void generateKeys(int size) {

        //First we need to select two random prime numbers
        BigInteger firstPrime = generatePrime(size/16);
        BigInteger secondPrime = generatePrime(size/16);
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
        publicKey = new Pair<String,String>(e.toString(),n.toString());
        System.out.println("Private key is {d,n} " + d.toString() + " "+ n.toString());
        privateKey = new Pair<String,String>(d.toString(),n.toString());
        this.e = e.toString();
        this.d = d.toString();
        this.n = n.toString();

    }

    public void Encrypt(String message){
        //Use Sha on the message : int code = Sha(message)
        int code = 12; //example of possible encoding of the message by the sha
        int e = Integer.parseInt(this.e);
        BigInteger n = new BigInteger(this.n);


        //C is our encrypted message
        BigInteger c = new BigInteger(Integer.toString(code));
        c = c.pow(e);
        c = c.mod(n);

        System.out.println("Encrypted Message is " + c);
        this.encryptedMessage = c.toString();

    }

    public void Decrypt(){
        BigInteger n = new BigInteger(this.n);
        BigInteger d = new BigInteger(this.d);
        BigInteger c = new BigInteger(this.encryptedMessage);

        BigInteger result = BigInteger.ONE;
        while(d.signum() > 0){
            if(d.testBit(0)){result=result.multiply(c);}
            c = c.multiply(c);
            d=d.shiftRight(1);
        }

        result = result.mod(n);
        System.out.println("Decrypted Message is " + result);

    }

    public Pair<String,String> getPublicKey(){
        return publicKey;
    }

    public Pair<String,String> getPrivateKey(){
        return privateKey;
    }

}
