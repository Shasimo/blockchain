public class Score {
    private int player1;

    private int player2;

    private int referee;

    Score(){

    }

    //Getters
    public int GetScorePlayer1(){
        return player1;
    }

    public int GetScorePlayer2(){
        return player2;
    }

    public int GetScoreReferee(){
        return referee;
    }

    //Setters
    public void setPlayer1(int score){
        player1=score;
    }

    public void setPlayer2(int score){
        player2=score;
    }

    public void setReferee(int score){
        referee=score;
    }

    // Json/DB
    public void Serialize(){

    }


}
