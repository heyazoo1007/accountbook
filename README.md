# 💰AccountBook 

매일의 지출내역을 사용자가 직접 기록하는 프로젝트입니다.
매달 혹은 연말에 그동안의 지출 내역과 카테고리별 지출내역을 알려줘서 본인의 소비습관을 알고, 더 좋은 소비습관을 만들어갈 수 있게 도와주는 프로젝트를 만들고 싶었습니다.

</br>

뱅크샐러드를 참조하여 생각한 프로젝트인데요, 뱅크샐러드처럼 카드와 계좌를 연동해서 지출 내역을 자동 업로드하기에는 지금 제 수준에서는 어렵다고 생각해 사용자가 직접 입력하는 것으로 변경했습니다.

</br>

## 목적 
* Spring Security, JWT를 이용한 로그인 로직 이해 
* Redis로 cache 구현 
* SQL group by 를 이용해 데이터 조작 방법 익히기 

</br>

## Details
🗓️ 작업기간 : 2022.12.21 ~ 2023.01.23
</br>
👨‍💻 투입인원 : 1명

</br>

## Skills

- Java8
- SpringBoot 2.7.7, Spring Data JPA, Spring Security
- JWT
- MariaDB, Redis 7.0.5
- QueryDSl
- Test(Junit5, Mockito)
- Swagger (API Documentation)
- GitHub 
- IntelliJ
- ERDCloud, Postman 

</br>

## Software Architecture


</br>

## ERD
<img width="800" alt="스크린샷 2023-01-29 오후 5 24 32" src="https://user-images.githubusercontent.com/81020108/215315154-d19975ed-f604-4592-9b10-e74d6469f6f2.png">

</br>

## Convention
- Git Convention: [link](https://mixed-leotard-ccd.notion.site/Code-Convention-791c4d57f67f43b88b14d73a2688d4fa)
- Code Convention: [link](https://mixed-leotard-ccd.notion.site/Git-Convention-7f6d010fb870485ba4b152a37a274cec)

</br>

## API Documentation
- API Docs : [link](https://mixed-leotard-ccd.notion.site/API-84da8d061e894bcfa5aecddadc2bc79c)

</br>

## 주요 기능 

공통 
* 기능 구현 후 단위 테스트 코드 작성 
* 공통 예외처리  - [상세보기](https://github.com/heyazoo1007/accountbook/wiki/%EA%B3%B5%ED%86%B5-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC)
* 공통 응답처리 - [상세보기](https://github.com/heyazoo1007/accountbook/wiki/%EA%B3%B5%ED%86%B5-%EC%9D%91%EB%8B%B5%EC%B2%98%EB%A6%AC)

</br>

회원가입 - [상세보기](https://github.com/heyazoo1007/accountbook/wiki/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85)
* 이메일 중복과 인증 절차를 진행합니다.
* 인증까지 완료된 사용자만 회원가입이 가능합니다. 
* 이름, 이메일, 비밀번호를 입력할 수 있습니다. 

</br>

로그인 
* 회원가입한 시용자만 로그인 가능합니다.
* 이메일과 비밀번호가 일치하는지 확인합니다.
* 위 조건을 다 통과한다면 JWT access 토큰을 응답합니다. 

</br>

회원 
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 본인의 정보만 작업할 수 있습니다. 
* 회원 정보 조회 
    * 이름, 이메일, 비밀번호, 이번달 예산 가능 
* 회원 정보 재설정 
    * 이름, 비밀번호, 예산 가능 
    * 이메일은 재설정 불가능 
* 회원 탈퇴 
    * 사용자가 가지고 있는 모든 정보를 삭제합니다

</br>

예산
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 본인의 예산만 작업할 수 있습니다. 
* 매달 시작할 때 예산을 설정할 수 있습니다. 
* 생성된 예산에 한해 수정 가능합니다.

</br>

매일 지출내역 - [상세보기](https://github.com/heyazoo1007/accountbook/wiki/%EB%A7%A4%EC%9D%BC-%EC%A7%80%EC%B6%9C%EB%82%B4%EC%97%AD)
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 본인의 매열 지출내역만 작업할 수 있습니다. 
* 지출내역을 CRUD 할 수 있습니다. 
* 선택한 달의 모든 지출내역 리스트를 조회할 수 있습니다. 
* 조회시 DB의 부하를 줄일 수 있게 캐시를 구현했습니다. 

</br>

검색 
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 키워드로 지출내역을 검색할 수 있습니다. 
* 거래처와 메모에 키워드를 포함하는 지출내역 리스트를 응답합니다. 

</br>

카테고리 
* 로그인한 회원만 사용할 수 있는 기능입니다.
* 본인의 카테고리만 작업할 수 있습니다. 
* 카테고리 CRUD를 할 수 있습니다. 

</br>

Monthly 지출내역 - [상세보기](https://github.com/heyazoo1007/accountbook/wiki/Monthly-%EA%B2%B0%EC%82%B0)
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 본인의 매열 지출내역만 작업할 수 있습니다. 
* 한달치 지출내역을 합산해 카테고리 별로 분류해서 조회합니다. 

</br>

Yearly 총 결산 
* 로그인한 회원만 사용할 수 있는 기능입니다. 
* 본인의 매열 지출내역만 작업할 수 있습니다. 
* 이전년도들만 조회 가능합니다. 
* 올해를 포함한 미래는 조회 불가능합니다.
* 이전에 저장한 총 지출내역과 카테고리별 지출 중 해당 년도의 값을 가져온 뒤 합산합니다.
* 예) 1위 식비(xxx만원), 2위 쇼핑(xxx만원),,,



