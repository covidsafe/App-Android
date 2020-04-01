package edu.example.covidsafe.comms;

import edu.uw.covidsafe.comms.CommunicationConfig;
import edu.uw.covidsafe.comms.QueryBuilder;

import org.junit.Test;

public class QueryBuilderTest {
    @Test
    public void testUserRegistration() {
        CommunicationConfig config = new CommunicationConfig("localhost", 50051, "Localhost server");
        QueryBuilder queryBuilder = new QueryBuilder(config);
        queryBuilder.registerUser();
    }
}
