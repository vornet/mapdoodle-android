package com.vornet.mapdoodle;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
/**
 * Created by Vorn on 7/17/2016.
 */
public class FakeTest {
    @Test
    public void fakeTest_True_IndeedTrue() {
        assertThat(true, is(true));
    }
}