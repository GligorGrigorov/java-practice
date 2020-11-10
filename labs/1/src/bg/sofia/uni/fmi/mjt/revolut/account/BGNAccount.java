package bg.sofia.uni.fmi.mjt.revolut.account;

public class BGNAccount extends Account {

    public BGNAccount(String IBAN){
        super(IBAN,0);
    }
    public BGNAccount(String IBAN, double amount){
        super(IBAN,amount);
    }
    @Override
    public String getCurrency() {
        return "BGN";
    }
}
