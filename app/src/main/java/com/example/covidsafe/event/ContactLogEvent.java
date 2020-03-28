package com.example.covidsafe.event;

import com.example.corona.comms.BLTContactLog;

import java.util.List;

public class ContactLogEvent {
    private boolean requestStatus;
    private List<BLTContactLog> contactLogs;

    public void setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
    }

    public List<BLTContactLog> getContactLogs() {
        return contactLogs;
    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public void setContactLogs(List<BLTContactLog> contactLogs) {
        this.contactLogs = contactLogs;
    }
}
