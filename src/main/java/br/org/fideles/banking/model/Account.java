package br.org.fideles.banking.model;

import java.math.BigDecimal;

public class Account {

//    private Long id;
    private Long id = 0L;

    private String accountNumber;

    private String ownerName;

    private BigDecimal balance = BigDecimal.ZERO;

    public Account() {}

    public Account(long id, String accountNumber, String ownerName, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance.compareTo(BigDecimal.ZERO) > 0 ? balance : new BigDecimal(0L);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountNumber: " + this.getAccountNumber() + "\n" +
               "Owner Name: " + this.getOwnerName() + "\n" +
                "Balance: " + this.getBalance().toString() ;
    }
}



