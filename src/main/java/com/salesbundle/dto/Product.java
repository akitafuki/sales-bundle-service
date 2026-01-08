package com.salesbundle.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    @JsonProperty("machine_name")
    private String machineName;

    @JsonProperty("tile_name")
    private String title;

    @JsonProperty("product_url")
    private String url;

    @JsonProperty("tile_image")
    private String imageUrl;

    @JsonProperty("high_res_tile_image")
    private String highResImageUrl;

    @JsonProperty("marketing_blurb")
    private String marketingBlurb;

    @JsonProperty("detailed_marketing_blurb")
    private String detailedMarketingBlurb;

    @JsonProperty("author")
    private String author;

    @JsonProperty("highlights")
    private List<String> highlights;

    @JsonProperty("start_date|datetime")
    private String startDate;

    @JsonProperty("end_date|datetime")
    private String endDate;

    private String category;
}