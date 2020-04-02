package edu.uw.covidsafe.utils;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class CryptoUtilsTest {

    @Test
    public void chainGenerateUUIDFromSeed() {
        List<String> ss = CryptoUtils.chainGenerateUUIDFromSeed(
                UUID.randomUUID().toString(),10);
        for(String s : ss) {
            System.out.println(s);
        }
    }
}