package state;

public class JavaCallState {
    private int responseDelay;

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    public int responseDelay() {
        return responseDelay;
    }
}
