package com.eaphonetech.common.datatables.samples.jpa.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eaphonetech.common.datatables.model.mapping.DataTablesInput;
import com.eaphonetech.common.datatables.model.mapping.DataTablesOutput;
import com.eaphonetech.common.datatables.samples.jpa.entities.Order;
import com.eaphonetech.common.datatables.samples.jpa.repo.OrderRepository;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OrderRestController {

    @Autowired
    private OrderRepository repo;

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/data/orders", method = RequestMethod.GET)
    public DataTablesOutput<Order> getOrders(@Valid DataTablesInput input,
            @RequestParam(required = false) Date startDate, @RequestParam(required = false) Date endDate) {
        Specification<Order> spec = between(startDate, endDate);

        return repo.findAll(input, spec);
    }

    private Specification<Order> between(Date startDate, Date endDate) {
        return new Specification<Order>() {
            private static final long serialVersionUID = -4988268222460360078L;

            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if (startDate != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(""), startDate));
                }
                if (endDate != null) {
                    predicates.add(criteriaBuilder.lessThan(root.get(""), endDate));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    /**
     * Insert some data to Fongo
     */
    @PostConstruct
    public void insertSampleData() {
        log.debug("initializing default data...");

        // some random orders
        for (int i = 0; i < 200; i++) {
            Order o = Order.random();

            repo.save(o);
        }

        // some orders with specific values
        Order o = Order.random();
        o.setOrderNumber("O10001");
        repo.save(o);

        o = Order.random();
        o.setOrderNumber("O10002");
        repo.save(o);

        log.debug("default data successfully initialized.");
    }
}
