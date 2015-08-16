package input;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.Player.Player;

/**
 * Created by Isaac Assegai on 8/14/2015.
 * This does okay for approximating game replays, however there seems
 * to be some element of non-determinism, so the replays are not exact, but noticibly
 * different.
 */
public class GameInput {
    public enum InputType{KEYPRESSED, KEYRELEASED}
    private InputType type;
    private int keycode;
    private int frameNum; //The frame this event happened on
    private float timeStamp;

    //Player record fields here
    private Vector2 loc;
    private Vector2 linearVelocity;
    private float angularVelocity;
    private float angle;

    public GameInput(InputType type, int keycode, int frameNum, float timeStamp, Player player){

        this.type = type;
        this.keycode = keycode;
        this.timeStamp = timeStamp;
        this.frameNum = frameNum;
        this.loc = player.getBody().getPosition().cpy();
        //this.loc = new Vector2(player.getX(), player.getY());
        this.linearVelocity = player.getBody().getLinearVelocity().cpy();
        this.angularVelocity = player.getBody().getAngularVelocity();
        this.angle = player.getBody().getAngle();
        //System.out.println("adding input for loc: " + loc.x + " " + loc.y);
    }

    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public int getKeycode() {
        return keycode;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(int frameNum) {
        this.frameNum = frameNum;
    }

    public Vector2 getLoc() {
        return loc;
    }

    public void setLoc(Vector2 loc) {
        this.loc = loc;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}