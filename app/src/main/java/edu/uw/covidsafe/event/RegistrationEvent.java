package edu.uw.covidsafe.event;

public class RegistrationEvent {
    private boolean requestStatus;
//    private Registered response;

    public void setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
    }

//    public void setResponse(Registered response) {
//        this.response = response;
//    }

//    public Registered getResponse() {
//        return response;
//    }

    public boolean isRequestStatus() {
        return requestStatus;
    }
}
