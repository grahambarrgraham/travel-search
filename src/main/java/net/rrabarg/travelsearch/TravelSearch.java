package net.rrabarg.travelsearch;

import com.google.common.collect.Streams;

import java.util.List;
import java.util.stream.Collectors;

interface Offer {
    long price();

    long duration();

    default int transits() { return 1; };

    default int bookings() { return 1; }

    List<Leg> legs();
}

record CombineQuote(List<SingleQuote> quotes, List<Leg> legs) implements Offer {

    @Override
    public long price() {
        return quotes.stream().mapToLong(SingleQuote::price).sum();
    }

    @Override
    public long duration() {
        return legs.stream().mapToLong(Leg::duration).sum();
    }

    @Override
    public int transits() {
        return (int) quotes.stream().mapToLong(Offer::transits).sum();
    }

    @Override
    public int bookings() {
        return (int) quotes.stream().map(SingleQuote::vendor).distinct().count();
    }

}

record SingleQuote(List<Leg> legs, String vendor, long price) implements Offer {

    @Override
    public long price() {
        return price;
    }

    @Override
    public long duration() {
        return legs.stream().mapToLong(Leg::duration).sum();
    }

    @Override
    public int transits() {
        return (int) legs.stream().mapToLong(Leg::transits).sum();
    }
}

record Goal(List<Constraint> constraints) {

    record Pair(Constraint constraint, Leg leg) {
        boolean fulfilled() {
            return constraint.fulfilled(leg);
        }
    }
    public boolean fulfilled(Offer offer) {
        if (constraints.size() != offer.legs().size()) {
            return false;
        }
        return Streams.zip(constraints.stream(), offer.legs().stream(), Pair::new)
                .allMatch(Pair::fulfilled);
    }
}

record Constraint(String from, String to, long dep, long arr) {
    public boolean fulfilled(Leg offer) {
        return offer.from().equals(from) && offer.to().equals(to) && offer.dep() >= dep && offer.arr() <= arr;
    }
};

record Leg(String from, String to, long dep, long arr, int transits) {
    public long duration() {
        return arr - dep;
    }

    public static Leg combine(Leg a, Leg b) {
        return new Leg(a.from, b.to, a.dep, b.arr, a.transits + b.transits);
    }
}

public class TravelSearch {
    public List<Offer> search(Goal goal, List<Offer> quotes) {
        return quotes
                .stream()
                .collect(Collectors.partitioningBy(goal::fulfilled))
                .get(true);
    }
}
