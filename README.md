# 🚩 Swagger 주소

http://ec2-13-209-88-90.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

# 📁 Entity Relationship Diagram

![img_1.png](img_1.png)

# 🧾 Endpoints

HTTP | Endpoint              | 설명         |
--- |-----------------------|------------| 
POST | api/v1/users/join     | 회원가입       |
POST | api/v1/users/login    | 로그인        |
POST | api/v1/posts          | 포스트 등록     |
GET | api/v1/posts/{postId} | 포스트 한 개 조회 |
PUT | api/v1/posts/{postId} | 포스트 수정     |
DELETE | api/v1/posts/{postId} | 포스트 삭제     |
GET | api/v1/posts          | 포스트 모두 조회  |

# 💡 미션 요구사항 분석 & 체크리스트

- Swagger 적용 [O]
  - API 문서 자동화
- Dockerfile 추가, 변경사항 발생 시 자동으로 Docker 빌드해서 이미지 생성하게끔 설정 [O]
- JPA 사용할 수 있도록 application.yml 파일에서 설정값들 변경 [O]
- 회원가입 기능 구현 [O]
  - 패스워드 암호화 기능 구현
- 로그인 구현 [O]
  - JWT 활용, 토큰 생성 시 비밀 키로 암호화하도록 구현
- 포스트 등록 기능 구현 [O]
  - 권한이 있는 사용자만 포스트를 작성할 수 있는 API 작성 
- 포스트 작성 시 등록 날짜와 시간이 저장되는 기능 구현 [O]
  - JPA Auditing 기능 활용
- 포스트 조회 기능 구현 [O]
  - 포스팅된 게시물 한 개 조회하는 기능
  - 등록된 모든 포스트를 조회하는 기능
    - 포스트 조회는 비회원과 회원 모두 가능하도록 구현
- 등록된 포스트를 수정하는 기능 구현 [O]
- 등록된 포스트 삭제 기능 구현 [O]

# 🎨 1주차 미션 요약

## 준비 단계

### 1. 브랜치 및 이슈 트래커 사용

- **수행 과정**
```
1. 위 체크리스트에 명시된 기능 각각을 구현하기 전에 GitLab 의 이슈 생성 기능을 활용하여 해결할 문제 요약
2. 해결할 문제 별로 브랜치를 생성하여 코드의 수정은 생성된 브랜치에서 문제를 해결될 때까지 진행
3. 해결이 된 후 브랜치는 미션 1 브랜치인 sprint1 에 병합
```
- **중점에 둔 사항**
```
1. 이슈를 생성할 때 각 이슈별로 명시된 것 이상의 기능을 구현하지 않도록 유의    
2. 최대한 작은 단위로 기능을 묶어서 이슈로 등록 후 구현하도록 함
```

### 2. CI/CD 적용

- **수행 과정**
``` 
1. 프로젝트 배포의 자동화를 위해 GitLab의 CI 구축 기능인 파이프라인 에디터로 YAML 파일을 작성
2. YAML 파일 작성을 위해 tutorialspoint 와 stackoverflow 를 참고
3. Docker Build 후 이미지를 생성하였으므로 docker docs 공식문서를 참고하여 Dockerfile 을 작성
```
- **참고 문헌**

https://www.tutorialspoint.com/yaml/yaml_indentation_and_separation.htm
https://stackoverflow.com/questions/42247535/yaml-how-many-spaces-per-indent


## 구현 단계

### 회원가입 및 로그인 기능

### 1) **POST api/v1/users/join**

기능:

- 사용할 아이디와 비밀번호로 사용자를 회원으로 등록

요구 사항:

> 회원 가입에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "userId": 23,
    "userName": "itsme"
  }
}
```
 
> 회원 가입에 실패하는 경우:
 
 1. 중복된 아이디로 회원가입 시도가 발생했을 때

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "DUPLICATE_USERNAME",
    "message": "itsme는 이미 존재하는 아이디입니다."
  }
}
```


### 2) **POST api/v1/users/login**

기능:

- 입력된 아이디와 비밀번호로 로그인하여 토큰을 발급

요구 사항:

- 로그인이 성공적으로 이루어졌을 때 다음과 같이 동작한다:
 
 1. 입력한 아이디 정보가 들어 있는 JSON Web token 이 발급된다.
 2. "SUCCESS" 메시지와 토큰 값이 함께 JSON 형태 데이터로 응답된다.

> 로그인에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdHJpbmcyIiwiaWF0IjoxNjcyNjUwMzUwLCJleHAiOjE2NzI2NTM5NTB9.Zum-eL9J-eVODdQoTM2zRQs3qkoQXWV2UFfV00DxWsk"
  }
}
```
 
> 로그인에 실패하는 경우:
 
 1. 회원가입 되지 않은 사용자 아이디가 입력되었을 때
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "USERNAME_NOT_FOUND",
    "message": "strig는 등록되지 않은 아이디입니다."
  }
}
```

 2. 아이디와 비밀번호가 회원가입 시에 입력한 내용과 일치하지 않을 때

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PASSWORD",
    "message": "패스워드가 잘못되었습니다."
  }
}
```

참고 문헌:

https://itistori.tistory.com/37

### 3) **POST api/v1/posts**

기능:

- 포스트를 등록함

요구 사항:

- Spring Security 를 활용해 토큰을 가지고 인증 절차를 거쳐야만 포스트 등록이 가능하도록 구현:

1. JWT 를 사용하여, Bearer 로 시작하는 토큰 값을 서버에서 요청 시 헤더에 담는다.

> 포스트 등록에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 등록 완료",
    "postId": 68
  }
}
```

> 포스트 등록에 실패하는 경우:
 
1. 토큰으로 인증 절차를 거치지 않은 사용자가 API 를 호출해 등록을 시도할 때

```json
{
  "timestamp": "2023-01-02T09:25:30.413+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/v1/posts"
}
```

2. 만료된 토큰을 사용하는 사용자가 API 를 호출해 등록을 시도할 때

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```

중점 사항:

- 토큰 필터에서 발생한 예외를 클라이언트가 알아볼 수 있는 JSON 형태로 반환하도록 구현 

참고 문헌:

https://medium.com/@mypascal2000/custom-handling-of-invalid-jwt-in-spring-boot-f66e60d59230

### 4) **GET api/v1/posts/{postId}**

기능:

- 아이디 postId 의 포스트의 상세 정보를 조회함 

요구 사항:

- 조회 시 포스트의 다음 정보를 알 수 있다:
 
  - 아이디<br>
  - 제목<br>
  - 내용<br>
  - 작성자<br>
  - 등록 날짜<br>
  - 마지막 수정 날짜

> 포스트 조회에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "id": 1,
    "title": "title",
    "body": "body",
    "userName": "userName",
    "createdAt": "2022-12-26T17:44:24.595322",
    "lastModifiedAt": null // 수정한 적이 없음
  }
}
```

> 포스트 조회에 실패하는 경우:
1. 입력된 아이디로 조회되는 포스트가 없을 때
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```

중점 사항:

- 성공 시와 에러 시 모두 JSON 형태의 응답을 반환하도록 구현 


### 5) **PUT api/v1/posts**

기능:

> 포스트를 수정함

요구 사항: 포스트를 작성한 사용자로 로그인 되어 있을 때만 포스트 수정이 가능하도록 구현

> 포스트 수정에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 등록 완료",
    "postId": 67
  }
}
```

> 포스트 수정에 실패하는 경우:
 
 1. 현재 로그인한 사용자가 작성자가 아닐 때

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```

 2. 입력된 고유 아이디로 수정하고자 하는 포스트를 찾을 수 없을 때 

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```
중점 사항:

- 포스트 수정이 되면 작성자와 고유 아이디는 그대로 남고 제목과 내용 및 수정 날짜/시간만 업데이트 되게 구현  


### 6) **DELETE api/v1/posts**

기능:

> 포스트를 삭제함

요구 사항:

> 포스트 삭제에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 삭제 완료",
    "postId": 66
  }
}
```

> 포스트 삭제에 실패하는 경우:<br>
1. 입력된 아이디의 포스트가 존재하지 않을 때 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```
2. 입력된 아이디로 찾은 포스트를 작성한 사용자로 로그인 되어 있지 않을 때 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```

### 7) **GET api/v1/posts**


기능:

- DB에 등록된 모든 포스트를 조회함

요구사항:

> 모든 포스트 조회에 성공하는 경우:

```json
{
  "resultCode": "SUCCESS",
  "result": {
    "content": [
      {
        "id": 68,
        "title": "test title",
        "body": "test body",
        "userName": "string2",
        "createdAt": "2023-01-02T09:27:51.670942",
        "lastModifiedAt": "2023-01-02T09:27:51.670942"
      },
      {
        "id": 67,
        "title": "string2",
        "body": "string",
        "userName": "string2",
        "createdAt": "2023-01-02T09:06:11.887259",
        "lastModifiedAt": "2023-01-02T09:06:11.887259"
      },
      {
        "id": 51,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T08:11:08.499926",
        "lastModifiedAt": "2022-12-27T08:11:08.499926"
      },
      {
        "id": 50,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T08:09:12.490571",
        "lastModifiedAt": "2022-12-27T08:09:12.490571"
      },
      {
        "id": 49,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:58:21.248993",
        "lastModifiedAt": "2022-12-27T07:58:21.248993"
      },
      {
        "id": 48,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:46:17.102629",
        "lastModifiedAt": "2022-12-27T07:46:17.102629"
      },
      {
        "id": 46,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:22:20.020946",
        "lastModifiedAt": "2022-12-27T07:22:20.020946"
      },
      {
        "id": 45,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:19:58.592971",
        "lastModifiedAt": "2022-12-27T07:19:58.592971"
      },
      {
        "id": 44,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:18:13.441116",
        "lastModifiedAt": "2022-12-27T07:18:13.441116"
      },
      {
        "id": 43,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:17:20.79841",
        "lastModifiedAt": "2022-12-27T07:17:20.79841"
      },
      {
        "id": 42,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:16:04.743408",
        "lastModifiedAt": "2022-12-27T07:16:04.743408"
      },
      {
        "id": 41,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:13:54.795969",
        "lastModifiedAt": "2022-12-27T07:13:54.795969"
      },
      {
        "id": 40,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:12:09.968914",
        "lastModifiedAt": "2022-12-27T07:12:09.968914"
      },
      {
        "id": 39,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:10:24.596607",
        "lastModifiedAt": "2022-12-27T07:10:24.596607"
      },
      {
        "id": 38,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:09:06.23095",
        "lastModifiedAt": "2022-12-27T07:09:06.23095"
      },
      {
        "id": 37,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:08:06.889224",
        "lastModifiedAt": "2022-12-27T07:08:06.889224"
      },
      {
        "id": 36,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:06:41.100342",
        "lastModifiedAt": "2022-12-27T07:06:41.100342"
      },
      {
        "id": 35,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:03:24.656661",
        "lastModifiedAt": "2022-12-27T07:03:24.656661"
      },
      {
        "id": 34,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T07:01:31.318696",
        "lastModifiedAt": "2022-12-27T07:01:31.318696"
      },
      {
        "id": 33,
        "title": "hello-title",
        "body": "hello-body",
        "userName": "kyeongrok22",
        "createdAt": "2022-12-27T06:50:32.575795",
        "lastModifiedAt": "2022-12-27T06:50:32.575795"
      }
    ],
    "pageable": "INSTANCE",
    "last": true,
    "totalPages": 1,
    "totalElements": 20,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "first": true,
    "numberOfElements": 20,
    "empty": false
  }
}
```

# 📝 회고

## 1. 테스트 코드
- ``Fixture`` 클래스를 사용해서 서비스 테스트 내에서 매번 ``setUp()`` 에서 객체를 생성하는 일을 줄여야 했는데, 결국 시간 관계상 지저분하게 내버려두었다.
- ``PostControllerTest`` 에서 모든 등록된 포스트를 조회할 때 최신순으로 포스트 정보들이 나열되는지를 테스트하지 못했다.

## 2. 컨트롤러
- 등록된 모든 포스트를 조회하여 나열할 때, 최신 순이 아니라 포스트의 고유 아이디가 높은 순대로 정렬하도록 ``@PageDefault`` 에서 설정했다.

## 3. 예외 처리
- 포스트를 수정하거나 삭제할 때에 데이터베이스 상에서 오류가 날 수 있는 상황에 대해 생각해보았으나, 결국 생각해내지 못했다.<br>
  요구 사항에 충실해서 처리해야 하는 예외 상황들을 모두 잡고 싶었지만 요구 사항에 대한 이해의 부족에서 온 실패라고 생각한다.

## 4. Merge Request
- 각 기능별로 이슈를 만들었고, 프로젝트 기간동안 이슈별로 브랜치를 만들고 작업이 완료된 후 미션 1의 브랜치인 ``sprint1``에 병합하는 방식을 사용했다.<br>
  중간에 하나의 기능에 대한 이슈 브랜치를 ``sprint1`` 이 아닌 ``main`` 브랜치에 병합을 하는 **대형사고(!)** 가 발생해서 ``revert`` 를 사용했고,<br>
  ``main`` 브랜치의 디렉토리에 ``revert`` 기록이 남게 되었다. 마지막에 ``sprint1`` 을 ``main`` 으로 병합할 때 ``revert`` 기록과 ``sprint1`` 에서 진행된 수정사항들이 충돌해서<br>
  ``merge conflict`` 가 발생했다.

## 5. README 를 사용한 문서화
- 코드 작성과 문서 작성을 동시에 하자.

## 6. 계획적인 코드 작성
- 매일 주어진 미션을 완료하는 것을 목표로 코드를 작성해야 제출일에 맞춰서 모든 기능 요구사항들을 충족시킬 수 있다.

> 고집을 내려놓고, 많은 구글링과 질문을 활용해서 구현하는 것이 정신 건강에도,<br>
양질의 프로그램을 구현하는 데에도 도움이 되는 것을 깨달았다.<br>
2주차에는 요구되는 기능들을 미리 구현한 후 밑에 적은 해 보고 싶은 사항들을 실천하겠다.

<hr>

# 🔍 해 보고 싶은 사항

## AOP 적용

> 관심사의 분리와 모듈화 에 대해 하나도 고민하지 않고 코드를 작성했다.<br>
관점 지향 프로그래밍으로 추상화 정도가 높아서 분리와 재사용이 가능한 코드를 짜고 싶다.