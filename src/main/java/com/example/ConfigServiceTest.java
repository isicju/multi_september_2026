package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.TreeSet;

@JCStressTest
@Outcome(
        id = "1",
        expect = Expect.ACCEPTABLE,
        desc = "Maps are same from getInstance"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Maps are different from getInstance"
)
@State
public class ConfigServiceTest {

    private ThreadSafetyPuzzles.ConfigService myMap1;
    private ThreadSafetyPuzzles.ConfigService myMap2;

    public ConfigServiceTest() {
        ;
    }

    @Actor
    public void writer1() {
        this.myMap1 = ThreadSafetyPuzzles.ConfigService.getInstance();
    }

    @Actor
    public void writer2() {
        this.myMap2 = ThreadSafetyPuzzles.ConfigService.getInstance();
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = this.myMap1 == this.myMap2 ? 1 : 0;
    }

}
