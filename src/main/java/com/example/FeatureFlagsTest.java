package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;
import org.openjdk.jcstress.infra.results.I_Result;
import org.openjdk.jcstress.infra.results.LL_Result;
import org.openjdk.jcstress.infra.results.ZZ_Result;

@JCStressTest
@Outcome(
        id = {"true, true"},
        expect = Expect.ACCEPTABLE,
        desc = "Both element been stored"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Some elements were/was lost"
)
@State
public class FeatureFlagsTest {

    private ThreadSafetyPuzzles.FeatureFlagsService myMap1;

    public FeatureFlagsTest() {
        this.myMap1 = new ThreadSafetyPuzzles.FeatureFlagsService();
    }

    @Actor
    public void writer1() {
        myMap1.update("1",true);

    }

    @Actor
    public void writer2() {
        myMap1.update("2",true);
    }

    @Arbiter
    public void arbiter(ZZ_Result r) {
        r.r1 = myMap1.isEnabled("1");
        r.r2 = myMap1.isEnabled("2");
    }


}
