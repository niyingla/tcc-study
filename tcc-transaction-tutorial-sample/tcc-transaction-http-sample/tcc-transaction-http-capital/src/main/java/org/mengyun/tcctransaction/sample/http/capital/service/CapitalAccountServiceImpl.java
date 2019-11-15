package org.mengyun.tcctransaction.sample.http.capital.service;

import org.mengyun.tcctransaction.sample.capital.domain.repository.CapitalAccountRepository;
import org.mengyun.tcctransaction.sample.http.capital.api.CapitalAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
public class CapitalAccountServiceImpl implements CapitalAccountService{

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Override
    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalAccountRepository.findByUserId(userId).getBalanceAmount();
    }


    @PostConstruct
    public void ddd(){
        SimpleHttpInvokerServiceExporter capitalAccountServiceExporter = (SimpleHttpInvokerServiceExporter) applicationContext.getBean("capitalAccountServiceExporter");
        CapitalAccountService service = (CapitalAccountService) capitalAccountServiceExporter.getService();
        BigDecimal capitalAccountByUserId = service.getCapitalAccountByUserId(2000);
        System.out.println(capitalAccountByUserId);
    }


}
