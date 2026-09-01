import os
import pandas as pd

# 💡 [핵심 수정] 파이썬 파일이 위치한 폴더의 '절대 경로'를 자동으로 계산하여 지정합니다.
FOLDER_PATH = os.path.dirname(os.path.abspath(__file__))

def check_excel():
    print("=" * 70)
    print("🔍 [홍수 엑셀 검증] 파일 및 데이터 구조 확인")
    print("=" * 70)
    print(f"📂 대상 폴더 경로: {FOLDER_PATH}\n")

    # 1. 대상 폴더 존재 여부 확인
    if not os.path.exists(FOLDER_PATH):
        print(f"❌ 폴더를 찾을 수 없습니다: {FOLDER_PATH}")
        return

    # 2. 'flood_shelter'로 시작하고 확장자가 xlsx 또는 xls인 파일 탐색
    excel_files = [
        f for f in os.listdir(FOLDER_PATH)
        if f.startswith("flood_shelter") and f.endswith(('.xlsx', '.xls'))
    ]

    if not excel_files:
        print("❌ 'flood_shelter'로 시작하는 엑셀 파일을 찾을 수 없습니다.")
        print(f"📁 현재 폴더 내 전체 파일 목록: {os.listdir(FOLDER_PATH)}")
        return

    total_count = 0

    # 3. 각 엑셀 파일 열람 및 데이터 검증
    for file in excel_files:
        file_path = os.path.join(FOLDER_PATH, file)
        try:
            # openpyxl 엔진으로 엑셀 로드
            df = pd.read_excel(file_path, engine='openpyxl')

            print(f"✅ 파일명: {file}")
            print(f"   - 총 데이터 수: {len(df):,}건")
            print(f"   - 컬럼 목록: {', '.join(df.columns.tolist())}")
            print(f"   - 샘플 데이터 (상위 1건):")
            print(df.head(1).to_string(index=False))
            print("-" * 70)

            total_count += len(df)

        except Exception as e:
            print(f"❌ {file} 파일 읽기 실패: {e}")
            print("-" * 70)

    print(f"🎉 엑셀 파일 검증 완료! (총 {len(excel_files)}개 파일 / 누적 합계: {total_count:,}건)")

if __name__ == "__main__":
    check_excel()