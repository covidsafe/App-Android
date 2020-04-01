package edu.example.covidsafe.performance;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class UUIDTest {
    @Test
    public void test_uuid_intersection_performance() {
        Set<UUID> uuidset = new HashSet<>();
        Set<UUID> subset = new HashSet<>();

        for (int i=0; i<1000; i++) {
            UUID r = UUID.randomUUID();
            uuidset.add(r);
            if (i%100 == 0) {
                subset.add(r);
            }
        }

        long s = System.currentTimeMillis();
        uuidset.retainAll(subset);
        long e = System.currentTimeMillis();

        System.out.println("Time taken : " + (e-s) + " ms");

        assertEquals(uuidset.size(), subset.size());
    }
}
