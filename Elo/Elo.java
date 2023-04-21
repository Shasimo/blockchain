public class Elo {

    private float elo;
    private final String player;

    //Constructor used by new players where the elo = 0
    Elo(String player){
        this.player = player;
        this.elo = 0;

    }

    public float getElo() {
        return this.elo;
    }

    public void setElo(float elo) {
        this.elo = elo;
    }

    public String getPlayer() {
        return this.player;
    }
}
