-- 1. 공습 대피소 테이블
CREATE TABLE IF NOT EXISTS airstrike (
                                         shlt_id VARCHAR(255) PRIMARY KEY,
    ctpv_nm VARCHAR(255),
    sgg_nm VARCHAR(255),
    fclt_nm VARCHAR(255),
    daddr VARCHAR(255),
    lot VARCHAR(255),
    lat VARCHAR(255),
    mng_dept_nm VARCHAR(255),
    se INT DEFAULT 2
    );

-- 2. 지진 대피소 테이블
CREATE TABLE IF NOT EXISTS earthquake (
                                          shlt_id VARCHAR(255) PRIMARY KEY,
    ctpv_nm VARCHAR(255),
    sgg_nm VARCHAR(255),
    fclt_nm VARCHAR(255),
    daddr VARCHAR(255),
    lot VARCHAR(255),
    lat VARCHAR(255),
    mng_dept_nm VARCHAR(255),
    se INT DEFAULT 3
    );

-- 3. 홍수 대피소 테이블
CREATE TABLE IF NOT EXISTS flood (
                                     shlt_id VARCHAR(255) PRIMARY KEY,
    ctpv_nm VARCHAR(255),
    sgg_nm VARCHAR(255),
    fclt_nm VARCHAR(255),
    daddr VARCHAR(255),
    lot VARCHAR(255),
    lat VARCHAR(255),
    mng_dept_nm VARCHAR(255),
    se INT DEFAULT 1
    );