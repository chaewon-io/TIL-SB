# DB 생성
DROP DATABASE IF EXISTS sbb;
CREATE DATABASE sbb;
USE sbb;

## 외래키 제약 끄기
SET FOREIGN_KEY_CHECKS = 0;

#각 테이블 초기화 //answer 먼저 삭제
TRUNCATE answer;
TRUNCATE question;

# 외래키 제약 복구
SET FOREIGN_KEY_CHECKS = 1;