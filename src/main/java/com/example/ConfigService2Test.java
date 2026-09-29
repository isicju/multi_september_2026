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
public class ConfigService2Test {

    private ThreadSafetyPuzzles.ConfigService myMap1;

    public ConfigService2Test() {

        this.myMap1 = ThreadSafetyPuzzles.ConfigService.getInstance();
    }


    @Actor
    public void writer1() {
        this.myMap1.set("1", "1");
    }

    @Actor
    public void writer2() {

        this.myMap1.set("2", "2");
    }

    @Arbiter
    public void arbiter(I_Result r) {
        Object first = myMap1.get("1");
        Object second = myMap1.get("2");
        r.r1 = first != null && second != null ? 2 : 0;
    }


}
