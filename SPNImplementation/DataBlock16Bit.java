public class DataBlock16Bit 
{
    private int data;

    public DataBlock16Bit() 
    {
        data = 0;
    }
    public DataBlock16Bit(int in) 
    {
        data = in;
    }
    public DataBlock16Bit(String in) 
    {
        int mul = (int)(1 << 15);
        for (int i = 0; i < 16; i++) 
        {
            data += ((int) in.charAt(i) - 48) * mul;
            mul >>>= 1;
        }
    }
    
    
    public int getShort() 
    {
        return data;
    }
    public boolean getBit(int index) 
    {
        if((data & (1 << (15 - index))) != 0) 
        {
            return true;
        }
        return false;
    }

    public int getBits(int startIndex, int endIndex) 
    {
        int out = 0;
        int mul = (int) (1 << (endIndex - startIndex - 1));
        for (int i = startIndex; i < endIndex; i++)
        {
            if(getBit(i)) 
            {
                out += mul;
            }
            mul >>>= 1;
        }
        return (int)(out);
    }

    public void setBit(int index, boolean value) 
    {
        if (value)
            data |= (1 << (15 - index));
        else
            data &= ((1 << 16) - 1 - (1 << (15 - index)));
    }

    public void printDataBlock(boolean withLineBreak) 
    {
        int mul = (int) (1 << 15);
        String x = Integer.toBinaryString(mul);
        for (int i = 0; i < 16; i++) 
        {
            if((data & mul) != 0) 
            {
                System.out.print('1');
            } 
            else 
            {
                System.out.print('0');
            }
            mul >>>= 1;
            x = Integer.toBinaryString(mul);
        }
        if(withLineBreak)
            System.out.println();
    }

    public static DataBlock16Bit xor(DataBlock16Bit in1, DataBlock16Bit in2) 
    {
        return (new DataBlock16Bit((int) (in1.getShort() ^ in2.getShort())));
    }
}
