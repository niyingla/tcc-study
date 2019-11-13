package org.mengyun.tcctransaction.sample.capital.domain.entity;



import org.mengyun.tcctransaction.sample.exception.InsufficientBalanceException;

import java.math.BigDecimal;

/**
 * Created by changming.xie on 4/2/16.
 */
public class CapitalAccount {

    private long id;

    private long userId;

    private BigDecimal balanceAmount;

    private BigDecimal transferAmount = BigDecimal.ZERO;

    public long getUserId() {
        return userId;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //扣金额
    public void transferFrom(BigDecimal amount) {
        //余额减去 金额
        this.balanceAmount = this.balanceAmount.subtract(amount);
        //如果余额小于0  报错
        if (BigDecimal.ZERO.compareTo(this.balanceAmount) > 0) {
            throw new InsufficientBalanceException();
        }
        //修改变动 金额
        transferAmount = transferAmount.add(amount.negate());
    }

    //加金额
    public void transferTo(BigDecimal amount) {
        this.balanceAmount = this.balanceAmount.add(amount);
        transferAmount = transferAmount.add(amount);
    }

    public void cancelTransfer(BigDecimal amount) {
        transferTo(amount);
    }
}
