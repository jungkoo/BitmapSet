package net.indf.collection.block;

/**
 * Created by 정민철 on 2015-01-02.
 */
public class Blocks {
    public static int BLOCK_LIMITED_SIZE    = getConfig("net.indf.collection.block.BLOCK_LIMITED_SIZE", 2048);
    public static int SOME_ITEM_SIZE        = getConfig("net.indf.collection.block.SOME_ITEM_SIZE",     10);
    
    
    
    private static int getConfig(String option, int defaultValue) {
        try{
            return Integer.parseInt(System.getenv(option));
        }catch (Exception e) {
            return defaultValue;
        }
    }
    
    
    

    
    
}
