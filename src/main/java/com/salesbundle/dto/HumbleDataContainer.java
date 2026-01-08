package com.salesbundle.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HumbleDataContainer {
    private CategoryContainer books;
    private CategoryContainer games;
    private CategoryContainer software;
}