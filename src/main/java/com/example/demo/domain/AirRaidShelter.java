package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "airstrike")
@Getter
@Setter
@NoArgsConstructor
public class AirRaidShelter {

    @Id
    @Column(name = "shlt_id") // 실제 DB의 PK 컬럼명 매핑 (수동 할당 방식)
    private String shltId;

    @Column(name = "ctpv_nm")
    private String ctpvNm;

    @Column(name = "sgg_nm")
    private String sggNm;

    @Column(name = "fclt_nm")
    private String fcltNm;

    @Column(name = "daddr")
    private String daddr;

    @Column(name = "lot")
    private Double lot; // 경도 (실수형)

    @Column(name = "lat")
    private Double lat; // 위도 (실수형)

    @Column(name = "mng_dept_nm")
    private String mngDeptNm;
}