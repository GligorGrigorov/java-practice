package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualOneTimeCard extends PhysicalCard{
    public VirtualOneTimeCard(String number, int pin, LocalDate expirationDate) {
        super(number, pin, expirationDate);
        type = "VIRTUALONETIME";
    }
}
