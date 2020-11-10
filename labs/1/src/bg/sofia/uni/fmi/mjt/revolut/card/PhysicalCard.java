package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class PhysicalCard implements Card{
    protected String number;
    protected String type;
    protected LocalDate expirationDate;
    protected int pin;
    protected Boolean isBlocked;
    protected int attempts;

    public PhysicalCard(String number, int pin, LocalDate expirationDate){
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
        isBlocked = false;
        type = "PHYSICAL";
        attempts = 0;
    }
    private int getAttempts(){
        return attempts;
    }

    private void setAttempts(int attempts){
        this.attempts = attempts;
    }
    @Override
    public String getType() {
        return type;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        boolean result = this.pin == pin;

        if(result){
            setAttempts(0);
        }else {
            setAttempts(getAttempts() + 1);
        }
        if(getAttempts() == 3){
            block();
        }
        return result;
    }

    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public void block() {
        isBlocked = true;
    }


}
