package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jcstress.infra.results.LL_Result;

@JCStressTest
@Outcome(
        id = {"A, A", "B, B"},
        expect = Expect.ACCEPTABLE,
        desc = "Both element been stored"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Some elements were/was lost"
)
@State
public class TrickyCacheTest {

    private ThreadSafetyPuzzles.TrickyCache myMap1;

    public TrickyCacheTest() {

        this.myMap1 = new ThreadSafetyPuzzles.TrickyCache();
    }


    @Actor
    public void writer1(LL_Result result) {
        myMap1.putIfAbsent("1","A");
        result.r1 = myMap1.get("1");

    }

    @Actor
    public void writer2(LL_Result result) {
        myMap1.putIfAbsent("1","B");
        result.r2 = myMap1.get("1");
    }


}
