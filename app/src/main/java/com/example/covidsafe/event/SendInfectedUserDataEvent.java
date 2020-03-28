package com.example.covidsafe.event;

import com.example.corona.comms.UserDataSent;

public class SendInfectedUserDataEvent {
    private boolean requestStatus;
    private UserDataSent response;

    public UserDataSent getResponse() {
        return response;
    }

    public void setResponse(UserDataSent response) {
        this.response = response;
    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
    }
}
