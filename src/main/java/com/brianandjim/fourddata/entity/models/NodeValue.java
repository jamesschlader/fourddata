package com.brianandjim.fourddata.entity.models;

import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NodeValue {
    @Id
    @GeneratedValue
    @GraphQLQuery
    private Long nodeValueId;
}
