/*
* Here are three steps to generating Elo ratings:
* Expected score: predict the outcome of a game.
* Actual score: observe the outcome.
* Update: increase or decrease each player’s rating based on result.
*
* */
public class EloRating{

    //Expected Score : E_{A} described below.
    private static float Probability(float rating1, float rating2) {
        return 1.0f / (1 + (float) (Math.pow(10, (rating1 - rating2) / 400)));
    }

    //K is a constant that can be changed
    public static void Rating(Elo player1, Elo player2 , String victoriousPlayer , float k){

        // To calculate the Winning
        // Probability of Player 2
        float Pb = Probability(player1.getElo(), player2.getElo());

        // Probability of Player 1
        //Pa = E_{A} = 1 / 1 + 10^((R_{B} - R_{A} ) / D ) where D has the value 400 in most of the elo systems but it can be changed.
        float Pa = Probability(player2.getElo(), player1.getElo());

        //Case where Player 1 won the game
        // If a player with higher ELO rating wins, only a few points are transferred from the lower rated player.
        // However if lower rated player wins, then transferred points from a higher rated player are far greater.
        // S_{a} = {1 if Player 1 won , 0 otherwise } and we have R_{A}' = R_{A} + K * (S_{A} - E_{A}) for R_{B}' is the same but we replace R_{A} with R_{B}
        if(victoriousPlayer==player1.getPlayer()){
            player1.setElo(player1.getElo() + k * (1-Pa));
            //We don't allow negative elo rating, so we do a quick check on the loser's new elo to not be < 0
            if((player2.getElo() + k * (0-Pb)) >= 0){
                player2.setElo(player2.getElo() + k * (0-Pb));
            }else{
                player2.setElo(0);
            }

        }else{
            if((player1.getElo() + k * (0-Pa)) >= 0){
                player1.setElo(player1.getElo() + k * (0-Pa));
            }
            else{
                player1.setElo(0);
            }
            player2.setElo(player2.getElo() + k * (1-Pb));
        }



    }


}
