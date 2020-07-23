package randall.util;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestRandallUtil {
    
    static Stream<Arguments> paramsForMerge() {
        return Stream.of(Arguments.of(Lists.newArrayList("asd", "fgh"), ","));
    }
    
    static Stream<Arguments> paramsForSplit() {
        return Stream.of(Arguments.of("BBBAaabnb", "Aaa"),
                         Arguments.of("bbbaaabnb", "a"),
                         Arguments.of("123.*456", ".*"),
                         Arguments.of("123.*456", "\\"),
                         Arguments.of("", ","));
    }
    
    @ParameterizedTest
    @MethodSource("paramsForMerge")
    void testMergeAgainstOriginalImplementation(ArrayList<String> list, String merge) {
        Assertions.assertEquals(test.randall.util.RandallUtil.merge(list, merge), RandallUtil.merge(list, merge));
    }
    
    @ParameterizedTest
    @MethodSource("paramsForSplit")
    void testSplitAgainstOriginalImplementation(String input, String separator) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(test.randall.util.RandallUtil.split(input, separator, false), RandallUtil.split(input, separator, false)),
                () -> Assertions.assertEquals(test.randall.util.RandallUtil.split(input, separator, true), RandallUtil.split(input, separator, true))
        );
    }
}