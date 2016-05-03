package EncryptionAndDecryption;

public class SPNImplementer16Bit {

    private static int[][][] sBox;
    private static int[][][] inverseSBox;
    
    public SPNImplementer16Bit() 
    {
        sBox = new int[][][]{
            {
                {10, 4, 0, 11, 8, 2, 9, 14, 1, 13, 15, 12, 7, 6, 5, 3},
                {5, 14, 15, 9, 6, 3, 7, 13, 12, 11, 10, 8, 4, 0, 2, 1},
                {13, 4, 6, 1, 14, 2, 15, 11, 5, 8, 9, 7, 0, 3, 12, 10},
                {7, 5, 1, 13, 2, 14, 9, 12, 6, 11, 4, 0, 8, 3, 10, 15}
            },
            {
                {5, 4, 9, 14, 8, 7, 3, 12, 0, 1, 2, 13, 10, 11, 6, 15},
                {5, 12, 4, 6, 2, 8, 11, 15, 10, 13, 3, 14, 7, 9, 0, 1},
                {10, 2, 14, 11, 4, 5, 7, 15, 13, 12, 9, 0, 3, 1, 8, 6},
                {9, 5, 0, 12, 7, 10, 2, 4, 14, 1, 13, 6, 11, 3, 8, 15}
            },
            {
                {13, 8, 11, 7, 12, 4, 1, 2, 0, 14, 9, 15, 6, 3, 5, 10},
                {10, 4, 11, 9, 14, 2, 7, 3, 0, 8, 12, 13, 6, 1, 15, 5},
                {14, 15, 5, 1, 0, 13, 2, 9, 11, 12, 4, 8, 3, 10, 7, 6},
                {12, 5, 0, 7, 3, 2, 13, 1, 8, 15, 10, 9, 4, 6, 14, 11}
            },
            {
                {4, 9, 12, 5, 11, 1, 8, 3, 13, 6, 2, 10, 14, 7, 15, 0},
                {15, 4, 9, 12, 13, 14, 7, 0, 11, 1, 8, 6, 5, 10, 2, 3},
                {12, 10, 7, 14, 5, 1, 6, 0, 8, 15, 11, 3, 13, 4, 9, 2},
                {1, 11, 3, 9, 10, 7, 5, 12, 4, 6, 2, 0, 8, 14, 15, 13}
            }
        };
        inverseSBox = new int[][][]{
            {
                {2, 8, 5, 15, 1, 14, 13, 12, 4, 6, 0, 3, 11, 9, 7, 10},
                {13, 15, 14, 5, 12, 0, 4, 6, 11, 3, 10, 9, 8, 7, 1, 2},
                {12, 3, 5, 13, 1, 8, 2, 11, 9, 10, 15, 7, 14, 0, 4, 6},
                {11, 2, 4, 13, 10, 1, 8, 0, 12, 6, 14, 9, 7, 3, 5, 15}
            },
            {
                {8, 9, 10, 6, 1, 0, 14, 5, 4, 2, 12, 13, 7, 11, 3, 15},
                {14, 15, 4, 10, 2, 0, 3, 12, 5, 13, 8, 6, 1, 9, 11, 7},
                {11, 13, 1, 12, 4, 5, 15, 6, 14, 10, 0, 3, 9, 8, 2, 7},
                {2, 9, 6, 13, 7, 1, 11, 4, 14, 0, 5, 12, 3, 10, 8, 15}
            },
            {
                {8, 6, 7, 13, 5, 14, 12, 3, 1, 10, 15, 2, 4, 0, 9, 11},
                {8, 13, 5, 7, 1, 15, 12, 6, 9, 3, 0, 2, 10, 11, 4, 14},
                {4, 3, 6, 12, 10, 2, 15, 14, 11, 7, 13, 8, 9, 5, 0, 1},
                {2, 7, 5, 4, 12, 1, 13, 3, 8, 11, 10, 15, 0, 6, 14, 9}
            },
            {
                {15, 5, 10, 7, 0, 3, 9, 13, 6, 1, 11, 4, 2, 8, 12, 14},
                {7, 9, 14, 15, 1, 12, 11, 6, 10, 2, 13, 8, 3, 4, 5, 0},
                {7, 5, 15, 11, 13, 4, 6, 2, 8, 14, 1, 10, 0, 12, 3, 9},
                {11, 0, 10, 2, 8, 6, 9, 5, 12, 3, 4, 1, 7, 15, 13, 14}
            }
        };
    }

    private static DataBlock16Bit permutation(DataBlock16Bit in) 
    {
        DataBlock16Bit out = new DataBlock16Bit();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                out.setBit(4 * j + i, in.getBit(4 * i + j));
            }
        }
        
        return out;
    }

    private DataBlock16Bit round(int roundIndex, DataBlock16Bit in, DataBlock16Bit subkey) 
    {
        DataBlock16Bit temp = new DataBlock16Bit();
        DataBlock16Bit out = new DataBlock16Bit();
        int val = 0;
        
        temp = DataBlock16Bit.xor(in, subkey);
        val ^= (sBox[roundIndex][0][temp.getBits(0, 4)] << 12);
        val ^= (sBox[roundIndex][1][temp.getBits(4, 8)] << 8);
        val ^= (sBox[roundIndex][2][temp.getBits(8, 12)] << 4);
        val ^= (sBox[roundIndex][3][temp.getBits(12, 16)] << 0);
        
        out = new DataBlock16Bit(val);
        out = permutation(out);
        
        return out;
    }
    private DataBlock16Bit inverseRound(int roundIndex, DataBlock16Bit in, DataBlock16Bit subkey)
    {
        DataBlock16Bit temp = new DataBlock16Bit();
        DataBlock16Bit out = new DataBlock16Bit();
        
        temp = permutation(in);
        int val = 0;
        val ^= (inverseSBox[roundIndex][0][temp.getBits(0, 4)] << 12);
        val ^= (inverseSBox[roundIndex][1][temp.getBits(4, 8)] << 8);
        val ^= (inverseSBox[roundIndex][2][temp.getBits(8, 12)] << 4);
        val ^= (inverseSBox[roundIndex][3][temp.getBits(12, 16)] << 0);
        
        out = new DataBlock16Bit(val);
        out = DataBlock16Bit.xor(out, subkey);
        
        return out;
    }
    
    public DataBlock16Bit encrypt(DataBlock16Bit plainText, DataBlock16Bit[] subkey)
    {
        DataBlock16Bit out = plainText;
        
        out = round(0, out, subkey[0]);
        out = round(1, out, subkey[1]);
        out = round(2, out, subkey[2]);
        out = round(3, out, subkey[3]);
        
        return out;
    }
    
    public DataBlock16Bit decrypt(DataBlock16Bit cipherText, DataBlock16Bit[] subkey)
    {
        DataBlock16Bit out = cipherText;
        
        out = inverseRound(3, out, subkey[3]);
        out = inverseRound(2, out, subkey[2]);
        out = inverseRound(1, out, subkey[1]);
        out = inverseRound(0, out, subkey[0]);
    
        return out;
    }
}
