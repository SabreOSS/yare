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

package com.sabre.oss.yare.examples.facts;

import java.math.BigDecimal;
import java.util.Objects;

public class Hotel {
    private String hotelName;
    private String chainCode;
    private boolean isPreferred;
    private BigDecimal roomRate;
    private String currency;

    public String getHotelName() {
        return hotelName;
    }

    public String getChainCode() {
        return chainCode;
    }

    public boolean getIsPreferred() {
        return isPreferred;
    }

    public BigDecimal getRoomRate() {
        return roomRate;
    }

    public String getCurrency() {
        return currency;
    }

    public Hotel withHotelName(final String hotelName) {
        this.hotelName = hotelName;
        return this;
    }

    public Hotel withChainCode(final String chainCode) {
        this.chainCode = chainCode;
        return this;
    }

    public Hotel withIsPreferred(final boolean isPreferred) {
        this.isPreferred = isPreferred;
        return this;
    }

    public Hotel withRoomRate(final BigDecimal roomRate) {
        this.roomRate = roomRate;
        return this;
    }

    public Hotel withCurrency(final String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hotel hotel = (Hotel) o;
        return isPreferred == hotel.isPreferred &&
                Objects.equals(hotelName, hotel.hotelName) &&
                Objects.equals(chainCode, hotel.chainCode) &&
                Objects.equals(roomRate, hotel.roomRate) &&
                Objects.equals(currency, hotel.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelName, chainCode, isPreferred, roomRate, currency);
    }
}
