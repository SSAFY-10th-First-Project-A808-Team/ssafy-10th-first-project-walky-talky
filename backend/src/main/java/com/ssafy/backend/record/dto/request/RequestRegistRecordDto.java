package com.ssafy.backend.record.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegistRecordDto {
    private Long seq;
    private String Duration;
    private Double Distance;
    private String[][] points;
    private int starRating;
    private String comment;
    private String title;
    private String regionCd;
}
