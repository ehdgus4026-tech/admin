import os
import urllib.parse
import pandas as pd
from sqlalchemy import create_engine
import sqlalchemy
import pymysql

# ============================================================
# [DB 접속 정보 설정]
# ============================================================
DB_USER = "root"
DB_PASSWORD = "root"
DB_HOST = "localhost"
DB_PORT = "3306"
DB_NAME = "shelter_db"

encoded_pw = urllib.parse.quote_plus(DB_PASSWORD)
DB_URL = f"mysql+pymysql://{DB_USER}:{encoded_pw}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

FOLDER_PATH = os.path.dirname(os.path.abspath(__file__))

def create_super_admin():
    """서버 구동 시 super 관리자 계정이 없으면 자동 생성하는 함수"""
    print("=" * 65)
    print("[Step 0] 최고 관리자(super) 계정 확인 및 생성 중...")
    print("=" * 65)
    try:
        connection = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            charset='utf8mb4'
        )
        with connection.cursor() as cursor:
            cursor.execute("SELECT COUNT(*) FROM user WHERE username = 'super'")
            result = cursor.fetchone()

            if result[0] == 0:
                sql = "INSERT INTO user (username, password, email, realname) VALUES (%s, %s, %s, %s)"
                cursor.execute(sql, ('super', 'super', 'super@admin.com', '최고관리자'))
                connection.commit()
                print(">>> [자동 생성] 최고 관리자(super) 계정이 데이터베이스에 등록되었습니다. (PW: super)")
            else:
                print(">>> [확인] 최고 관리자(super) 계정이 이미 존재합니다.")
        connection.close()
    except Exception as e:
        print(f"[ERROR] 관리자 계정 생성 에러: {e}")

def process_shelter_data(engine, prefix, table_name, prefix_id):
    """공통 대피소 엑셀 파일을 찾아 정제 후 지정된 테이블에 적재하는 함수"""
    print("\n" + "=" * 65)
    print(f"[Step] {table_name.upper()} 대피소 데이터 파일 탐색 및 로드 시작")
    print("=" * 65)

    target_file = None
    for f in os.listdir(FOLDER_PATH):
        if f.startswith(prefix) and (f.endswith(".xlsx") or f.endswith(".xls")):
            target_file = os.path.join(FOLDER_PATH, f)
            break

    if not target_file:
        print(f"[ERROR] {prefix} 엑셀 파일을 찾을 수 없습니다.")
        return

    print(f"[TARGET] 로드 대상 파일: {os.path.basename(target_file)}")
    raw_df = pd.read_excel(target_file, engine='openpyxl')
    print(f"[INFO] 원본 엑셀 데이터 {len(raw_df):,}건 로드 완료")

    print(f"[Step] MySQL 규격에 맞춰 {table_name} 데이터 정제 중...")
    clean_df = pd.DataFrame()

    if 'shlt_id' in raw_df.columns:
        clean_df['shlt_id'] = raw_df['shlt_id'].astype(str)
    elif 'id' in raw_df.columns:
        clean_df['shlt_id'] = raw_df['id'].astype(str)
    else:
        clean_df['shlt_id'] = [f"{prefix_id}_{i}" for i in range(1, len(raw_df) + 1)]

    clean_df['ctpv_nm'] = raw_df['ctpv_nm'].astype(str)
    clean_df['sgg_nm'] = raw_df['sgg_nm'].astype(str)
    clean_df['fclt_nm'] = raw_df['fclt_nm'].astype(str)
    clean_df['daddr'] = raw_df['daddr'].astype(str)

    # 지진 등 일부 데이터는 lat/lot가 문자열이나 공백일 수 있으므로 numeric 변환
    clean_df['lot'] = pd.to_numeric(raw_df['lot'], errors='coerce')
    clean_df['lat'] = pd.to_numeric(raw_df['lat'], errors='coerce')

    clean_df['mng_dept_nm'] = '-'

    # 테이블 구조에 'se' 컬럼이 있는 경우에만 추가 (airstrike, earthquake 등 스키마 차이 방어)
    if 'se' in raw_df.columns:
        clean_df['se'] = pd.to_numeric(raw_df['se'], errors='coerce').fillna(3).astype(int)
    else:
        clean_df['se'] = 3

    clean_df = clean_df.dropna(subset=['lat', 'lot', 'fclt_nm'])
    print(f"[OK] 정제 완료: 유효 데이터 {len(clean_df):,}건")

    print(f"[Step] MySQL {table_name} 테이블에 데이터 적재 중...")
    try:
        column_order = ['shlt_id', 'ctpv_nm', 'sgg_nm', 'fclt_nm', 'daddr', 'lot', 'lat', 'mng_dept_nm', 'se']
        # 혹시 테이블 스키마에 se 컬럼이 없다면 제외하고 맞춤
        actual_columns = [col for col in column_order if col in clean_df.columns]
        clean_df = clean_df[actual_columns]

        dtype_mapping = {
            'shlt_id': sqlalchemy.types.VARCHAR(255),
            'ctpv_nm': sqlalchemy.types.VARCHAR(255),
            'sgg_nm': sqlalchemy.types.VARCHAR(255),
            'fclt_nm': sqlalchemy.types.VARCHAR(255),
            'daddr': sqlalchemy.types.VARCHAR(500),
            'mng_dept_nm': sqlalchemy.types.VARCHAR(255),
            'lot': sqlalchemy.types.DOUBLE,
            'lat': sqlalchemy.types.DOUBLE,
            'se': sqlalchemy.types.Integer
        }

        clean_df.to_sql(name=table_name, con=engine, if_exists='replace', index=False, dtype=dtype_mapping)
        print(f"[SUCCESS] 총 {len(clean_df):,}건의 데이터가 '{table_name}' 테이블에 저장되었습니다!")
    except Exception as e:
        print(f"[ERROR] {table_name} DB 저장 에러: {e}")

def run_etl():
    create_super_admin()

    engine = create_engine(DB_URL)

    # 1. 홍수 대피소 적재
    process_shelter_data(engine, prefix="flood_shelter", table_name="flood", prefix_id="FL")

    # 2. 공습 대피소 적재 (파일명이 airstrike_shelter로 시작해야 함)
    process_shelter_data(engine, prefix="airstrike_shelter", table_name="airstrike", prefix_id="AS")

    # 3. 지진 대피소 적재 (파일명이 earthquake_shelter로 시작해야 함)
    process_shelter_data(engine, prefix="earthquake_shelter", table_name="earthquake", prefix_id="EQ")

if __name__ == "__main__":
    run_etl()