package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualPermanentCard  extends PhysicalCard{
    public VirtualPermanentCard(String number, int pin, LocalDate expirationDate) {
        super(number, pin, expirationDate);
        type = "VIRTUALPERMANENT";
    }
}
