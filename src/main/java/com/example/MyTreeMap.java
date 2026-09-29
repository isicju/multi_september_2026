package com.example;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

import java.util.TreeMap;

@JCStressTest
@Outcome(
        id = "2",
        expect = Expect.ACCEPTABLE,
        desc = "Expected value: 2 items"
)
@Outcome(
        id = ".*",
        expect = Expect.ACCEPTABLE_INTERESTING,
        desc = "Unexpected size!"
)
@State
public class MyTreeMap {

    private TreeMap<Integer,Integer> myMap;

    public MyTreeMap() {
        this.myMap = new TreeMap<>();
    }

    @Actor
    public void writer1() {
        try {
            myMap.put(1,1);
        } catch (Exception e) {
        }
    }

    @Actor
    public void writer2() {
        try {
            myMap.put(2,2);
        } catch (Exception e) {
        }
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = myMap.size();
    }

}
