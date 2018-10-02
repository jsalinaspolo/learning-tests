package state;

public interface ResultState {
    String successResult() throws Exception;
    Throwable thrownException();
}
