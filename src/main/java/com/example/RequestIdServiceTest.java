package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;
import org.openjdk.jcstress.infra.results.LL_Result;

@JCStressTest
@Outcome(
        id = {"2, 2"},
        expect = Expect.ACCEPTABLE,
        desc = "Both element been stored"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Some elements were/was lost"
)
@State
public class RequestIdServiceTest {

    private ThreadSafetyPuzzles.RequestIdService myMap1;

    public RequestIdServiceTest() {

        this.myMap1 = new ThreadSafetyPuzzles.RequestIdService();
    }


    @Actor
    public void writer1(II_Result result) {
        this.myMap1.newRequest();
        this.myMap1.newRequest();
        result.r1 = this.myMap1.currentId();
    }

    @Actor
    public void writer2(II_Result result) {
        this.myMap1.newRequest();
        this.myMap1.newRequest();
        result.r2 = this.myMap1.currentId();
    }


}
