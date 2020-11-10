package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;

import java.time.LocalDate;

public class Revolut implements RevolutAPI {
    Account[] accounts;
    Card[] cards;
    static final double EXCHANGE_RATE_EUR_BGN = 1.95583;
    static final double EXCHANGE_RATE_BGN_EUR = 1 / EXCHANGE_RATE_EUR_BGN;
    public Revolut(Account[] accounts, Card[] cards){
        this.accounts = accounts;
        this.cards = cards;
    }
    private int findAccountWithMinimalAmount(String currency){
        double minimalAmount = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i].getCurrency().compareTo(currency) == 0 && minimalAmount > accounts[i].getAmount()){
                index = i;
                minimalAmount = accounts[i].getAmount();
            }
        }
        return index;
    }
    @Override
    public boolean pay(Card card, int pin, double amount, String currency) {
        boolean isAvailable = false;
        boolean isPhysical = (card.getType().compareTo("PHYSICAL") == 0);
        for (int i = 0; i < cards.length; i++) {
            if(cards[i].hashCode() == card.hashCode()){
                isAvailable = true;
                break;
            }
        }

        boolean isValid = card.getExpirationDate().compareTo(LocalDate.now()) > 0;
        boolean haveMoney = false;
        for(int i = 0; i < accounts.length; i++) {
            if(accounts[i].getAmount() >= amount && accounts[i].getCurrency().compareTo(currency) == 0){
                haveMoney = true;
            }
        }
        if(isAvailable && isValid && !card.isBlocked() && card.checkPin(pin) && haveMoney && isPhysical){
            accounts[findAccountWithMinimalAmount(currency)].setAmount(accounts[findAccountWithMinimalAmount(currency)].getAmount() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        boolean isAvailable = false;
        boolean isValid = card.getExpirationDate().compareTo(LocalDate.now()) > 0;
        String[] elements = shopURL.split("[.]");
        boolean isBanned = false;
        if(elements.length > 0) {
            isBanned = (elements[elements.length - 1].compareTo("biz") == 0);
        }
        for (int i = 0; i < cards.length; i++) {
            if(cards[i].hashCode() == card.hashCode()){
                isAvailable = true;
                break;
            }
        }
        boolean haveMoney = false;
        for(int i = 0; i < accounts.length; i++) {
            if(accounts[i].getAmount() >= amount && accounts[i].getCurrency().compareTo(currency) == 0){
                haveMoney = true;
            }
        }
        if(isAvailable && isValid && !card.isBlocked() && card.checkPin(pin) && haveMoney && !isBanned){

            if (card.getType().compareTo("VIRTUALONETIME") == 0){
                card.block();
            }
            accounts[findAccountWithMinimalAmount(currency)].setAmount(accounts[findAccountWithMinimalAmount(currency)].getAmount() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean addMoney(Account account, double amount) {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].hashCode() == account.hashCode()) {
                accounts[i].setAmount(account.getAmount() + amount);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean transferMoney(Account from, Account to, double amount) {
        boolean fromExists = false;
        boolean toExists = false;
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i].hashCode() == from.hashCode()){
                fromExists = true;
            }
            if(accounts[i].hashCode() == to.hashCode()){
                toExists = true;
            }
        }
        if(!fromExists || !toExists || from.getIBAN().compareTo(to.getIBAN()) == 0 || from.getAmount() <= amount){
            return false;
        }

        if(to.getCurrency().compareTo(from.getCurrency()) == 0){
            to.setAmount(to.getAmount() + amount);
            from.setAmount(from.getAmount() - amount);
            return  true;
        }
        if(to.getCurrency().compareTo("BGN") == 0){
            to.setAmount(to.getAmount() + amount * EXCHANGE_RATE_EUR_BGN);
        }else {
            to.setAmount(to.getAmount() + amount * EXCHANGE_RATE_BGN_EUR);
        }
        from.setAmount(from.getAmount() - amount);
        return true;
    }

    @Override
    public double getTotalAmount() {
        double totalAmount = 0;
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].getCurrency().compareTo("EUR") == 0){
                totalAmount += accounts[i].getAmount() * EXCHANGE_RATE_EUR_BGN;
            }else {
                totalAmount += accounts[i].getAmount();
            }
        }
        return totalAmount;
    }
}
