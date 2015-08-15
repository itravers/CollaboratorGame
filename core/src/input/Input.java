package input;

/**
 * Created by Isaac Assegai on 8/14/2015.
 */
public class Input {

    enum InputType{KEYPRESSED, KEYRELEASED}
    private InputType type;
    private int keycode;
    private float timeStamp;
    public Input(InputType type, int keycode, float timeStamp){
        this.type = type;
        this.keycode = keycode;
        this.timeStamp = timeStamp;
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
}
