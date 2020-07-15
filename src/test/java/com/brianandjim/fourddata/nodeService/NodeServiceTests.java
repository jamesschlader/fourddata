package com.brianandjim.fourddata.nodeService;

import com.brianandjim.fourddata.entity.models.NodeValueSpace;
import com.brianandjim.fourddata.services.NodeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeServiceTests {

    private final NodeService nodeService;
    private List<NodeValueSpace> spaces = new ArrayList<>();

    public NodeServiceTests(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    @Test
    public void testAddValueToNode(){

    }
}
