package com.example.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class SHA256Hashing {

    public static void main(String[] args) {
        SHA256Hashing test = new SHA256Hashing("the quick red fox is jumping over the lazy brown dog");
        System.out.println(test.getBinaryDigest());
        System.out.println(test.getHexDigest());
    }

    private String hexDigest, binaryDigest;

    public SHA256Hashing(String messageToHash) {
        String binaryMessage = this.addPadding(this.stringToBinary(messageToHash));
        // setting the constants
        String[] h = {"6a09e667", "bb67ae85", "3c6ef372", "a54ff53a", "510e527f", "9b05688c", "1f83d9ab", "5be0cd19"};
        String[] k = {"428a2f98", "71374491", "b5c0fbcf", "e9b5dba5", "3956c25b", "59f111f1", "923f82a4", "ab1c5ed5",
                "d807aa98", "12835b01", "243185be", "550c7dc3", "72be5d74", "80deb1fe", "9bdc06a7", "c19bf174",
                "e49b69c1", "efbe4786", "0fc19dc6", "240ca1cc", "2de92c6f", "4a7484aa", "5cb0a9dc", "76f988da",
                "983e5152", "a831c66d", "b00327c8", "bf597fc7", "c6e00bf3", "d5a79147", "06ca6351", "14292967",
                "27b70a85", "2e1b2138", "4d2c6dfc", "53380d13", "650a7354", "766a0abb", "81c2c92e", "92722c85",
                "a2bfe8a1", "a81a664b", "c24b8b70", "c76c51a3", "d192e819", "d6990624", "f40e3585", "106aa070",
                "19a4c116", "1e376c08", "2748774c", "34b0bcb5", "391c0cb3", "4ed8aa4a", "5b9cca4f", "682e6ff3",
                "748f82ee", "78a5636f", "84c87814", "8cc70208", "90befffa", "a4506ceb", "bef9a3f7", "c67178f2"};
        // this part is not clean #TODO : find a way to fix this mess
        for (int i = 0; i < 8; i++) h[i] = Integer.toBinaryString(new BigInteger(h[i], 16).intValue());
        for (int i = 0; i < 64; i++) k[i] = Integer.toBinaryString(new BigInteger(k[i], 16).intValue());
        // preparing the message schedule
        String[] w = new String[64];
        int entrySize = 0;
        for (int i = 0; i < binaryMessage.length(); i += 512) {
            // creating a message schedule for the i chunk
            for (int j = i; j < i + 512; j += 32) {   // processing a 512 bits chunk
                w[entrySize] = binaryMessage.substring(j, j + 32);
                entrySize++;
            }
            // filling the array with zeroes
            for (int inc = 0; inc < 48; inc++) w[16 + inc] = reapeatNString("0",32);
            // compute [w[16]..w[64])
            for (int l = 16; l < 64; l++) {
                // Integer.parseInt(String, base) translates a String in a certain base to an int @deprecated
                //new BigInteger(String, base) does the same but works with 2's complement binaries
                BigInteger[] toSum = {getBigIntegerFromString(sigma0(w[l - 15])),
                        getBigIntegerFromString(w[l - 16]),
                        getBigIntegerFromString(sigma1(w[l - 2])),
                        getBigIntegerFromString(w[l - 7])};

                w[l] = module2exp32SUm(toSum);
            }
            // compression
            HashMap<Character, String> vars = new HashMap<>();
            char[] varsKeys = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
            // using a HashMap with letters as key for easier implementation
            int index = 0;
            while (index < 8) {
                h[index] = fillBinaryWithZeroes(h[index]);
                vars.put(varsKeys[index], h[index]);
                index++;
            }
            // end of compression setup
            for (int t = 0; t < 64; t++) {
                BigInteger[] t1ToSum = {getBigIntegerFromString(vars.get('h')),
                        getBigIntegerFromString(bigSigma1(vars.get('e'))),
                        getBigIntegerFromString(choose(vars.get('e'), vars.get('f'), vars.get('g'))),
                        getBigIntegerFromString(k[t]),
                        getBigIntegerFromString(w[t])};
                BigInteger[] t2ToSum = {getBigIntegerFromString(bigSigma0(vars.get('a'))),
                        new BigInteger(majority(vars.get('a'), vars.get('b'), vars.get('c')))};

                BigInteger t1 = getBigIntegerFromString(module2exp32SUm(t1ToSum));
                BigInteger t2 = getBigIntegerFromString(module2exp32SUm(t2ToSum));

                vars.replace('h', vars.get('g'));
                vars.replace('g', vars.get('f'));
                vars.replace('f', vars.get('e'));
                vars.replace('e', module2exp32SUm(new BigInteger[]{getBigIntegerFromString(vars.get('d')), t1}));
                vars.replace('d', vars.get('c'));
                vars.replace('c', vars.get('b'));
                vars.replace('b', vars.get('a'));
                vars.replace('a', module2exp32SUm(new BigInteger[]{t1, t2}));
            }
            for (int j = 0; j < 8; j++) {
                h[j] = Integer.toBinaryString(getBigIntegerFromString(module2exp32SUm(new BigInteger[]{
                        getBigIntegerFromString(vars.get(varsKeys[j])),
                        getBigIntegerFromString(h[j])})).intValue());
            }
        }
        StringBuilder hexDigest = new StringBuilder();
        StringBuilder binaryDigest = new StringBuilder();
        for (String elem : h) {
            binaryDigest.append(elem);
            hexDigest.append(Integer.toHexString(new BigInteger(elem, 2).intValue()));
        }
        this.hexDigest = hexDigest.toString();
        this.binaryDigest = binaryDigest.toString();
    }

    public String getHexDigest() {
        return hexDigest;
    }

    public String getBinaryDigest() {
        return binaryDigest;
    }

    private String fillBinaryWithZeroes(String binaryToFill) {
        int stringSize = binaryToFill.length();
        if (stringSize < 32) binaryToFill = reapeatNString("0",32 - stringSize) + binaryToFill;
        return binaryToFill;
    }


    private BigInteger getBigIntegerFromString(String binaryString) {
        return new BigInteger(binaryString, 2);
    }


    private String rightRotate(String binaryString, int n) {
        int intTranslation = getBigIntegerFromString(binaryString).intValue();
        intTranslation >>= n;
        int stringSize = binaryString.length();
        String hob = binaryString.substring(stringSize - n, stringSize);
        String lob = Integer.toBinaryString(intTranslation);
        return hob + lob;
    }

    private String module2exp32SUm(BigInteger[] toSum) {
        BigInteger sum = BigInteger.valueOf(0);
        for (BigInteger elem : toSum) sum = sum.add(elem);
        // might overflow from the int scope so, we use BigInteger toString instead of Integer.toBinaryString
        String binaryString = sum.toString(2);
        binaryString = fillBinaryWithZeroes(binaryString);
        int stringSize = binaryString.length();
        return binaryString.substring(stringSize - 32);
    }

    private String modulo2Sum(int[] toSum) {
        int sum = 0;
        for (int elem : toSum) sum = sum ^ elem;
        String binaryString = Integer.toBinaryString(sum);
        binaryString = fillBinaryWithZeroes(binaryString);
        int stringSize = binaryString.length();
        return binaryString.substring(stringSize - 32);
    }

    private String sigma0(String binaryString) {
        int[] binariesToSum = {getBigIntegerFromString(rightRotate(binaryString, 7)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 18)).intValue(),
                (getBigIntegerFromString(binaryString).intValue() >> 3)};
        return modulo2Sum(binariesToSum);
    }

    private String sigma1(String binaryString) {
        int[] binariesToSum = {getBigIntegerFromString(rightRotate(binaryString, 17)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 19)).intValue(),
                (getBigIntegerFromString(binaryString).intValue() >> 10)};
        return modulo2Sum(binariesToSum);
    }

    private String bigSigma0(String binaryString) {
        int[] binariesToSum = {getBigIntegerFromString(rightRotate(binaryString, 2)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 13)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 22)).intValue()};
        return modulo2Sum(binariesToSum);
    }


    private String bigSigma1(String binaryString) {
        int[] binariesToSum = {getBigIntegerFromString(rightRotate(binaryString, 6)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 11)).intValue(),
                getBigIntegerFromString(rightRotate(binaryString, 25)).intValue()};
        return modulo2Sum(binariesToSum);
    }


    private String choose(String choice, String first, String second) {
        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < choice.length(); i++) {
            if (choice.charAt(i) == '1') binary.append(first.charAt(i));
            else binary.append(second.charAt(i));
        }
        return binary.toString();
    }


    private String majority(String a, String b, String c) {
        StringBuilder binary = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            if ((a.charAt(i) == '1' && b.charAt(i) == '1')
                    || (a.charAt(i) == '1' && c.charAt(i) == '1')
                    || (b.charAt(i) == '1' && c.charAt(i) == '1')) binary.append('1');
            else binary.append('0');
        }
        return binary.toString();
    }


    private String addPadding(StringBuilder binaryString) {
        binaryString.append(1);
        int n = binaryString.length();
        int paddingSize = 0;
        int multiple = 1;
        while (paddingSize == 0) {  // equivalent to while true
            int differenceFromMultipleN = 512 * multiple - 64 - n;
            if (differenceFromMultipleN > 0 && (differenceFromMultipleN < 256 || multiple == 1)) {
                paddingSize = differenceFromMultipleN;
                break;
            }
            multiple++;
        }

        binaryString.append(reapeatNString("0",paddingSize));
        // adding the initial size of the string as a binary at the end (big endian)
        String initialLengthInBinary = Integer.toBinaryString(n);
        binaryString.append(reapeatNString("0",64 - initialLengthInBinary.length()));
        binaryString.append(initialLengthInBinary);
        return binaryString.toString();
    }


    private StringBuilder hexToBinary(byte[] hexMessage) {
        StringBuilder binaryMessage = new StringBuilder();
        for (byte b : hexMessage) {
            int hex = b;
            for (int i = 0; i < 8; i++) {
                binaryMessage.append((hex & 128) == 0 ? 0 : 1);
                hex <<= 1;
            }
        }
        return binaryMessage;
    } 

    private StringBuilder stringToBinary(String messageToHash) {
        byte[] hexMessage = messageToHash.getBytes(StandardCharsets.UTF_8);
        return hexToBinary(hexMessage);
    }

    private String reapeatNString(String stringToRepeat, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<n; i++) {
            stringBuilder.append(stringToRepeat);
        }
        return stringBuilder.toString();
    }

    public BigInteger getMessageBigInteger() {
        return new BigInteger(hexDigest, 16);
    }
}