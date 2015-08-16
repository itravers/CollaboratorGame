package input;

/**
 * Created by Isaac Assegai on 8/14/2015.
 */
public class GameInput {

    public enum InputType{KEYPRESSED, KEYRELEASED}
    private InputType type;
    private int keycode;
    private int frameNum; //The frame this event happened on
    private float timeStamp;
    public GameInput(InputType type, int keycode, int frameNum, float timeStamp){
        this.type = type;
        this.keycode = keycode;
        this.timeStamp = timeStamp;
        this.frameNum = frameNum;
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
}
