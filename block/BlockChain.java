import java.util.ArrayList;
public class BlockChain{

    //Array of all the blocks in the Chain
    private ArrayList<Block> BlockChain = new ArrayList<Block>();

    //Method to get the last block in the chain.
    public Block getLastBlock(){
        if(BlockChain.isEmpty()) return null;

        return BlockChain.get(BlockChain.size()-1);
    }

    public ArrayList<Block> getTheChain(){
        if(BlockChain.isEmpty()) return null;

        return BlockChain;
    }

    //We update the BlockChain with the chain.
    public void updateChain(Block newBlock){
        BlockChain.add(newBlock);
    }

    public void updateDB(){
        //Update the db
    }

    public void fetchDB(){
        //fetch the db
    }


}
