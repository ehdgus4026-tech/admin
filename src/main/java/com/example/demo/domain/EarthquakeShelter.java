package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // JPA 스펙 준수를 위한 기본 생성자 추가 권장
@Table(name = "earthquake")
public class EarthquakeShelter {

    @Id
    @Column(name = "shlt_id") // 실제 DB의 PK 컬럼명 매핑
    private String shltId;

    @Column(name = "ctpv_nm")
    private String ctpvNm;   // 시도명

    @Column(name = "sgg_nm")
    private String sggNm;    // 시군구명

    @Column(name = "fclt_nm")
    private String fcltNm;   // 대피소 이름

    @Column(name = "daddr")
    private String daddr;    // 주소

    @Column(name = "lot")
    private String lot;      // 경도 (DB 컬럼이 VARCHAR인 경우)

    @Column(name = "lat")
    private String lat;      // 위도 (DB 컬럼이 VARCHAR인 경우)

    @Column(name = "mng_dept_nm")
    private String mngDeptNm;// 관리부서
}