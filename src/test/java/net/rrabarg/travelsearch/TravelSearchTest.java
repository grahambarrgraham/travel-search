package net.rrabarg.travelsearch;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TravelSearchTest {

    private static final Vendor[] vendors = Vendor.values();
    private static final Random random = new Random();;

    enum Vendor {
        A, B, C, D, E, F, G
    }

    @Test
    public void testSingleLeg() {
        new TravelSearch();

        var q1 = new SingleQuote(List.of(new Leg("A", "B", 5, 10, 2)),anyVendor(), anyPrice());
        var q2 = new SingleQuote(List.of(new Leg("A", "C", 5, 10, 1)),anyVendor(), anyPrice());
        var q3 = new SingleQuote(List.of(new Leg("A", "B", 4, 10, 1)),anyVendor(), anyPrice());
        var q4 = new SingleQuote(List.of(new Leg("A", "B", 6, 10, 1)),anyVendor(), anyPrice());
        var q5 = new SingleQuote(List.of(new Leg("C", "B", 5, 10, 1)),anyVendor(), anyPrice());
        var q6 = new SingleQuote(List.of(new Leg("C", "D", 5, 10, 1)),anyVendor(), anyPrice());
        var q7 = new SingleQuote(List.of(new Leg("A", "B", 5, 11, 1)),anyVendor(), anyPrice());

        List<Offer> quotes = List.of(q1, q2, q3, q4, q5, q6, q7);
        var goal = new Goal(Collections.singletonList(new Constraint("A", "B", 5, 10)));
        var expected = List.of(q1, q4);

        assertEquals(expected, new TravelSearch().search(goal, quotes));
    }

    static long anyPrice() {
        return random.nextInt(20) + 5;
    }

    static String anyVendor() {
       return vendors[random.nextInt(vendors.length)].toString();
    }
}