# Today I Learn 'SBB'
스프링부트 기초를 다지기 위해 매일 공부합니다.  !!
교재 : [점프 투 스프링부트](https://wikidocs.net/book/7601)   
기간 : 2023년 06월 ~ 진행중  
기술 블로그 : [chaewon-io.log](https://velog.io/@chaewon22?tag=%EC%A0%90%ED%94%84%ED%88%AC%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8) 

<img width="1000" height="500" alt="스크린샷 2023-09-08 오전 12 13 26" src="https://github.com/chaewon-io/TIL-SB/assets/82140052/3247784d-0af6-4db2-a836-4864c4819837">       
  
스프링부트를 활용하여 게시판을 구현하는 '[점프 투 스프링부트](https://wikidocs.net/book/7601)' 책을 클론 코딩 후, SBB 추가 기능을 구현한 게시판 프로젝트 입니다. 

**기능 구현에 관한 자세한 코드 리뷰는 기술블로그([chaewon-io.log](https://velog.io/@chaewon22?tag=%EC%A0%90%ED%94%84%ED%88%AC%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8))에서 확인하실 수 있습니다.**

## 기술 스택

<div align=center> 
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white">
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
  <img src="https://img.shields.io/badge/lombok-C02E18?style=for-the-badge&logo=lombok&logoColor=white">
</div>

## ERD

<img width="744" alt="스크린샷 2023-09-08 오전 1 18 43" src="https://github.com/chaewon-io/TIL-SB/assets/82140052/7d9f4138-5eca-4317-9c9c-93fe9d585489">

## SBB 주요 기능 - 클론 코딩
- [x] 일반 회원가입 및 로그인
- [x] 질문 항목 - 등록, 조회(페이징), 수정, 삭제, 추천
- [x] 답변 항목 - 등록, 조회, 수정, 삭제, 추천
- [x] 질문 검색 - 질문 작성자, 내용, 제목, 답변 내용, 답변 작성자
- [x] 마크다운

## SBB 추가 기능 
- [x] 조회수
- [x] 프로필
- [x] 카테고리
- [x] 계정설정
  - [x] 한 줄 소개 저장
- [x] 비밀번호 변경 및 찾기
  - [x] 가입한 메일로 임시 비밀번호 전송
- [x] 소셜 로그인 - 카카오
  - [ ] 네이버 및 구글 로그인
- [ ] 답변 페이징과 정렬
- [ ] 최근 답변과 최근 댓글
- [ ] 마크다운 에디터

## SBB 배포
- [ ] 배포 사이트 예정
