package com.ssafy.backend.club.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseClubListDtoClub {
    private Long seq;
    private String name;
    private String url;
    private String introduce;
    private String address;
    private String youngBirth;
    private String oldBirth;
    private String genderType;
    private Integer nowCapacity;
    private Integer maxCapacity;
    private Boolean isAutoRecruit;
    private Boolean isOpenRecruit;
}
