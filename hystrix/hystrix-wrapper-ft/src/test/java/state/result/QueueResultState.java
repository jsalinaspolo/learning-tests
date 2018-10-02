package state.result;

import state.ResultState;

import java.util.concurrent.Future;

public class QueueResultState implements ResultState {
    private final Future<String> responseFuture;
    private final Throwable t;

    private QueueResultState(Future<String> responseFuture, Throwable t) {
        this.responseFuture = responseFuture;
        this.t = t;
    }

    public static ResultState successWith(Future<String> responseFuture) {
        return new QueueResultState(responseFuture, null);
    }

    public static ResultState failureWith(Throwable t) {
        return new QueueResultState(null, t);
    }

    public String successResult() throws Exception {
        return responseFuture.get();
    }

    public Throwable thrownException() {
        return t;
    }
}
