package com.ccp.WorkBridge.order;

import com.ccp.WorkBridge.order.dto.OrderSearchCriteria;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class OrderSpecification {

    public static Specification<Order> byCriteria(OrderSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getResultType() != Long.class) {
                query.distinct(true);
            }
            if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
                String pattern = "%" + criteria.getQuery().toLowerCase() + "%";
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), pattern);
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), pattern);
                predicates.add(cb.or(titlePredicate, descriptionPredicate));
            }
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }
            if (criteria.getOrderType() != null) {
                predicates.add(cb.equal(root.get("orderType"), criteria.getOrderType()));
            }
            if (criteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }
            if (criteria.getMinDeadline() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("deadline"), criteria.getMinDeadline()));
            }
            if (criteria.getMaxDeadline() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("deadline"), criteria.getMaxDeadline()));
            }
            if (criteria.getSkills() != null && !criteria.getSkills().isEmpty()) {
                Join<Order, OrderSkill> skillJoin = root.join("orderSkills", JoinType.INNER);
                List<Predicate> skillPredicates = new ArrayList<>();

                for (OrderSearchCriteria.SkillFilter f : criteria.getSkills()) {
                    Predicate skillMatch = cb.equal(skillJoin.get("skill").get("id"), f.skillId());
                    Predicate level = cb.conjunction();

                    if (f.minLevel() != null) {
                        level = cb.and(level, cb.greaterThanOrEqualTo(skillJoin.get("requiredLevel"), f.minLevel()));
                    }
                    if (f.maxLevel() != null) {
                        level = cb.and(level, cb.lessThanOrEqualTo(skillJoin.get("requiredLevel"), f.maxLevel()));
                    }
                    skillPredicates.add(cb.and(skillMatch, level));
                }
                predicates.add(cb.or(skillPredicates.toArray(new Predicate[0])));
                query.groupBy(root.get("id"));
                query.having(cb.equal(cb.countDistinct(skillJoin.get("skill").get("id")), criteria.getSkills().size()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
