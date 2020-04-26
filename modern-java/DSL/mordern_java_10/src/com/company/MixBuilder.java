package com.company;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class MixBuilder {
    public static Order forCustomer(String customer, TradeBuilder... builders) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(builders).forEach(b -> order.addTrade(b.trade));
        return order;
    }

    public static TradeBuilder buy(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.BUY);
    }

    public static TradeBuilder sell(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.SELL);
    }

    private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Trade.Type buy) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(buy);
        consumer.accept(builder);
        return builder;
    }

    public static void main(String[] args) {
        Order order = forCustomer("BigBank", buy(t -> t.quantity(80)
                                                                .stock("IBM")
                                                                .on("NYSE")
                                                                .at(125.00)),
                                                    sell( t -> t.quantity(50)
                                                                .stock("GOOGLE")
                                                                .on("NASDAQ")
                                                                .at(125.00)));
    }
}


