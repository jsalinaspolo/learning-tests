package state;

import org.slf4j.MDC;

public class MDCState {
    private static final String X_INT_REQUESTID = "x-int-requestid";
    public static final String FIRST_REQUESTID_VALUE = "first value";
    public static final String SECOND_REQUESTID_VALUE = "second value";
    private String mainThreadRequestId;
    private String commandThreadRequestId;

    public MDCState() {
        MDC.clear();
    }

    public void setupRequestId(String RequestIdValue) {
        mainThreadRequestId = RequestIdValue;
        MDC.put(X_INT_REQUESTID, mainThreadRequestId);
    }

    public void saveRequestId() {
        commandThreadRequestId = MDC.get(MDCState.X_INT_REQUESTID);
    }

    public String commandThreadRequestId() {
        return commandThreadRequestId;
    }

    public String mainThreadRequestId() {
        return mainThreadRequestId;
    }
}
