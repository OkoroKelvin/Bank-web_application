package com.digicorebankingapp.data.model;

public enum TransactionType {
    DEPOSIT, WITHDRAW;

    @Override
    public String toString() {
        switch (this) {
            case DEPOSIT: return "deposited";
            case WITHDRAW: return "withdrawn";
        }
        return null;
    }
}
