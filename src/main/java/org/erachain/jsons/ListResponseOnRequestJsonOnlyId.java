package org.erachain.jsons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ListResponseOnRequestJsonOnlyId {
    @JsonProperty("result")
    private List<ResponseOnRequestJsonOnlyId> responseOnRequestJsonOnlyIds = new ArrayList<>();

    public List<ResponseOnRequestJsonOnlyId> getResponseOnRequestJsonOnlyIds() {
        return responseOnRequestJsonOnlyIds;
    }
}
