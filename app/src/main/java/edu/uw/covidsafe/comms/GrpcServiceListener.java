package edu.uw.covidsafe.comms;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import edu.uw.covidsafe.comms.CommunicationConfig;
import edu.uw.covidsafe.comms.GetBLTContactLogsAsyncTask;
import edu.uw.covidsafe.comms.NetworkConstant;
import edu.uw.covidsafe.comms.QueryBuilder;
import edu.uw.covidsafe.event.ContactLogEvent;
import edu.uw.covidsafe.event.RegistrationEvent;
import edu.uw.covidsafe.event.SendInfectedLogEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class GrpcServiceListener extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
//        EventBus.getDefault().register(this);
//        Log.d("[GrpcServiceListener]", "Starting request for the listener");
//        CommunicationConfig config = new CommunicationConfig(NetworkConstant.HOSTNAME, NetworkConstant.PORT, "TestServer");
//        QueryBuilder queryBuilder = new QueryBuilder(config);
//        queryBuilder.registerUser();
//        queryBuilder.sendInfectedLogsOfUser();
//        queryBuilder.getBLTContactLogs();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        EventBus.getDefault().unregister(this);
        return true;
    }

//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    public void onReceivedRegisterUserResponse(RegistrationEvent registrationEvent) {
//        if (registrationEvent.isRequestStatus()) {
//            Log.d("[RegisterUserResponse] ", "Successfully received a response from server");
//            Registered response = registrationEvent.getResponse();
//            boolean isSuccess = response.getSuccess();
//            if (isSuccess) {
//                Log.d("[RegisterUserResponse] ", "Successfully registered the user");
//            } else {
//                Log.e("[RegisterUserResponse] ", "Unsuccessful in user registration");
//            }
//        } else {
//            Log.e("[RegisterUserResponse] ", "Failed to reach the server. Bad Internet connection or server downtime.");
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    public void onReceivedSendInfectedLogEvent(SendInfectedLogEvent sendInfectedLogEvent) {
//        if (sendInfectedLogEvent.isRequestStatus()) {
//            Log.d("[SendInfectedLog]", "Successfully received a response from server");
//            AddedLogs response = sendInfectedLogEvent.getResponse();
//            boolean isSuccess = response.getSuccess();
//            if (isSuccess) {
//                Log.d("[SendInfectedLog] ", "Successfully registered the user");
//            } else {
//                Log.e("[SendInfectedLog] ", "Unsuccessful in sending infected log");
//            }
//        } else {
//            Log.e("[SendInfectedLog] ", "Failed to reach the server. Bad Internet connection or server downtime.");
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    public void onReceivedBLTContactLogs(ContactLogEvent contactLogEvent) {
//        if (contactLogEvent.isRequestStatus()) {
//            Log.d("[BLTContactLog] ", "Successfully received a response from server");
//            List<BLTContactLog> logs = contactLogEvent.getContactLogs();
//            new GetBLTContactLogsAsyncTask(getApplicationContext(), logs).execute();
//        } else {
//            Log.e("[BLTContactLog] ", "Failed to reach the server. Bad Internet connection or server downtime.");
//        }
//    }
}
