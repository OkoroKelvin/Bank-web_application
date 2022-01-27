package com.digicorebankingapp.web.exception;


public class BankAppException  extends RuntimeException {
    public BankAppException(String message){
        super(message);
    }
    public BankAppException(){
        super();
    }
    public BankAppException(String message, Throwable e){
        super(message, e);
    }
    public BankAppException(Throwable e){
        super(e);
    }
}
