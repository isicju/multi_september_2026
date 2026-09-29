package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(
        id = "2",
        expect = Expect.ACCEPTABLE,
        desc = "Both element been stored"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Some elements were/was lost"
)
@State
public class RequestCounterTest {

    private ThreadSafetyPuzzles.RequestCounterService myMap1;

    public RequestCounterTest() {
        this.myMap1 = new ThreadSafetyPuzzles.RequestCounterService();
    }


    @Actor
    public void writer1() {
        myMap1.recordRequest();
    }

    @Actor
    public void writer2() {
        myMap1.recordRequest();
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = myMap1.getCount();
    }


}
