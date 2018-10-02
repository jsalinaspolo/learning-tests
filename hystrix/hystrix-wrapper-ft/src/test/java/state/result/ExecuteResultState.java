package state.result;

import state.ResultState;

public class ExecuteResultState implements ResultState {
    private final String response;
    private final Throwable t;

    private ExecuteResultState(String response, Throwable t) {
        this.response = response;
        this.t = t;
    }

    public static ResultState successWith(String response) {
        return new ExecuteResultState(response, null);
    }

    public static ResultState failureWith(Throwable t) {
        return new ExecuteResultState(null, t);
    }

    public String successResult() {
        return response;
    }

    public Throwable thrownException() {
        return t;
    }
}
