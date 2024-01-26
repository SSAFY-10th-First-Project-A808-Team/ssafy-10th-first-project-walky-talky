package com.ssafy.backend.record.domain;

import com.ssafy.backend.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "record_detail")
@Getter
@NoArgsConstructor
@Builder
public class RecordDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "record_seq", nullable = false)
    private Long recordSeq;

    private String latitude;

    private String longitude;

    @Column(name = "time")
    private String time;

    @Column(name = "url", length = 2083)
    private String url;

    @Column(name = "point_comment", length = 255)
    private String pointComment;

    public RecordDetail(Long seq, Long recordSeq, String latitude, String longitude, String time, String url, String pointComment) {
        this.seq = seq;
        this.recordSeq = recordSeq;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.url = url;
        this.pointComment = pointComment;
    }
}

