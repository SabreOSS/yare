/*
 * MIT License
 *
 * Copyright 2018 Sabre GLBL Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sabre.oss.yare.examples;

import com.sabre.oss.yare.core.model.Rule;
import com.sabre.oss.yare.dsl.RuleDsl;
import com.sabre.oss.yare.examples.facts.Airline;
import com.sabre.oss.yare.examples.facts.Car;
import com.sabre.oss.yare.examples.facts.Flight;
import com.sabre.oss.yare.examples.facts.Hotel;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.sabre.oss.yare.dsl.RuleDsl.*;

public final class RulesBuilder {

    static final String GET_AMOUNT_OF_MONEY = "getAmountOfMoney";
    static final String GET_DATE_DIFF_IN_HOURS = "getDateDiffInHours";
    static final String PRICE_DIFFERENCE = "priceDifference";
    static final String CONTAINS = "contains";
    static final String CONTAINS_ANY = "containsAny";
    static final String COLLECT = "collect";
    static final String SET_REJECTED_FLAG = "setRejectedFlag";

    private RulesBuilder() {
    }

    public static List<Rule> createRuleMatchingWhenAirlineCodesContainsGiven(List<String> airlineCodes) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        function(CONTAINS, Boolean.class,
                                                param("outerList", value("${airline.airlineCodes}")),
                                                param("innerList", values(String.class, airlineCodes))),
                                        value(true)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingWhenAirlineCodesInGiven(List<String> airlineCodes) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes are in given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        function(CONTAINS, Boolean.class,
                                                param("outerList", values(String.class, airlineCodes)),
                                                param("innerList", value("${airline.airlineCodes}"))),
                                        value(true)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingWhenAirlineCodesContainsAnyOfGiven(List<String> airlineCodes) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match airline when airline codes contain any of given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        function(CONTAINS_ANY, Boolean.class,
                                                param("outerList", value("${airline.airlineCodes}")),
                                                param("innerList", values(String.class, airlineCodes))),
                                        value(true)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${airline}")))
                        .build()
        );
    }

    public static List<Rule> createRuleSettingIsRejectedWhenNameEqualToGiven(String name) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should mark airline as rejected when its name is equal to given")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        value("${airline.name}"),
                                        value(name)
                                )
                        )
                        .action(SET_REJECTED_FLAG,
                                param("airline", value("${airline}")),
                                param("isRejected", value(true)))
                        .build()
        );
    }

    public static List<Rule> createRuleCollectingNotRejectedAirlines() {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match not rejected airlines")
                        .fact("airline", Airline.class)
                        .predicate(
                                equal(
                                        value("${airline.isRejected}"),
                                        value(false)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("airline", value("${airline}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingWithPriceOverLowest(BigDecimal howMuchOverLowestPrice) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match car with total rate greater than given over lowest price")
                        .fact("car", Car.class)
                        .predicate(
                                greater(
                                        function(PRICE_DIFFERENCE, BigDecimal.class,
                                                param("carRate", value("${car.totalRate}"))),
                                        value(howMuchOverLowestPrice)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${car}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingClassOfService(String classOfService) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match flight with given class of service")
                        .fact("flight", Flight.class)
                        .predicate(
                                equal(
                                        value("${flight.classOfService}"),
                                        value(classOfService)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingTimeUntilDepartureLowerThan(long timeUntilUntilDeparture) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match flight with time until departure in hours lower than given")
                        .fact("flight", Flight.class)
                        .predicate(
                                less(
                                        function(GET_DATE_DIFF_IN_HOURS, Long.class,
                                                param("givenDate", value("${flight.dateOfDeparture}"))),
                                        value(timeUntilUntilDeparture)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${flight}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingChainCode(String chainCode) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with given chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(chainCode)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingPreferredHotel(String preferredChainCode) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match preferred hotel basing on preferred property or chain code")
                        .fact("hotel", Hotel.class)
                        .predicate(or(
                                equal(
                                        value("${hotel.isPreferred}"),
                                        value(true)
                                ),
                                equal(
                                        value("${hotel.chainCode}"),
                                        value(preferredChainCode)
                                )
                        ))
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
    }

    public static List<Rule> createRuleMatchingHotelWithRoomRateGreaterThan(BigDecimal amount, String currency) {
        return Collections.singletonList(
                RuleDsl.ruleBuilder()
                        .name("Should match hotel with room rate greater than given")
                        .fact("hotel", Hotel.class)
                        .predicate(
                                greater(
                                        function(GET_AMOUNT_OF_MONEY, BigDecimal.class,
                                                param("amount", value("${hotel.roomRate}")),
                                                param("inputCurrency", value("${hotel.currency}")),
                                                param("outputCurrency", value(currency))),
                                        value(amount)
                                )
                        )
                        .action(COLLECT,
                                param("context", value("${ctx}")),
                                param("fact", value("${hotel}")))
                        .build()
        );
    }
}
