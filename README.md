# 🚩 Swagger 주소

http://ec2-13-209-88-90.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/

# 📝 요구 사항 정의서

## ⚡ 요구 기능

### 🌗 Sprint 1

1. Dockerfile 추가, GitHub Actions 사용해 변경사항 발생 시 자동으로 Docker 빌드해서 이미지 생성하게끔 설정 [O]
2. JPA 사용할 수 있도록 application.yml 파일에서 설정값들 변경 [O]
3. 모든 사용자는 회원가입할 수 있다. [O]
  - 패스워드 암호화 기능
4. 사용자는 로그인할 수 있다. [O]
  - JWT 활용, 토큰 생성 시 비밀 키로 암호화하도록 구현
5. 사용자는 포스트를 등록할 수 있다. [O]
  - 권한이 있는 사용자만 포스트를 작성할 수 있는 기능 [O]
  - 포스트 작성 시 등록 날짜와 시간이 저장되는 기능 [O]
    - JPA Auditing 기능 활용
6. 사용자는 포스트를 조회할 수 있다. [O]
  - 포스팅된 게시물 한 개 조회하는 기능
  - 등록된 모든 포스트를 조회하는 기능
    - 포스트 조회는 비회원과 회원 모두 가능
7. 사용자는 본인이 등록한 포스트를 수정할 수 있다. [O]
8. 사용자는 본인이 등록한 포스트를 삭제할 수 있다. [O]


### 🌕 Sprint 2

1. 모든 포스트에는 댓글을 달 수 있다. [O]
  - 댓글을 작성하는 기능 [O]
  - 댓글을 조회하는 기능 [O]
  - 댓글을 수정하는 기능 [O]
  - 댓글을 삭제하는 기능 [O]
2. 모든 포스트에는 좋아요를 누룰 수 있다 [O]
  - 좋아요를 누르는 기능 [O]
  - 포스트별로 눌린 좋아요의 개수를 보는 기능 [O]
3. 로그인한 유저는 자신이 작성한 포스트를 모아서 볼 수 있다. [O]
  - 모든 포스트 중 자신이 작성한 포스트만 모아서 조회하는 기능 [O]
4. 댓글이 달리거나 좋아요가 눌린 포스트의 작성자는 알람을 받아서 일시를 알 수 있다. [O]
5. 로그인하지 않았을 때 예외 발생, JSON 형태의 에러 내용을 출력하는 기능 [O]

## 📁 Entity Relationship Diagram

![img_1.png](img_1.png)

## 🧾 Endpoints

HTTP | Endpoint                            | 설명       |
--- |-------------------------------------|----------|
POST | api/v1/users/join     | 회원가입       |
POST | api/v1/users/login    | 로그인        |  
POST | api/v1/posts          | 포스트 등록     |  
GET | api/v1/posts/{postId} | 포스트 한 개 조회 |  
PUT | api/v1/posts/{postId} | 포스트 수정     |  
DELETE | api/v1/posts/{postId} | 포스트 삭제     |  
GET | api/v1/posts          | 포스트 모두 조회  |  
POST | api/v1/posts/{postId}/comments      | 댓글 작성    |
GET | api/v1/posts/{postId}/comments      | 댓글 조회    |
PUT | api/v1/posts/{postId}/comments/{id} | 댓글 수정    |
DELETE | api/v1/posts/{postId}/comments/{id} | 댓글 삭제    |
GET | api/v1/posts/my                     | 마이 피드 조회 |
POST | api/v1/posts/{postId}/likes         | 좋아요 등록   |
GET | api/v1/alarms                       | 알람 조회    |

## 주의 사항
- 포스트는 삭제 시 실제 삭제가 되지 않고 ``Soft Delete`` 적용하여 ``deleted_at`` 컬럼에 날짜와 시간이 표시되도록 함 (``default`` 는 ``null``)

관련 글


## 중점 사항
##### 문서화, 이슈 관리, 관심사의 분리

# 🚀 구현

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
  
- 참고 문헌: [Custom Handling of Invalid JWT in Spring Boot](https://medium.com/@mypascal2000/custom-handling-of-invalid-jwt-in-spring-boot-f66e60d59230)
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
    "content": [{
      "id": 68,
      "title": "test title",
      "body": "test body",
      "userName": "string2",
      "createdAt": "2023-01-02T09:27:51.670942",
      "lastModifiedAt": "2023-01-02T09:27:51.670942"
    }, {
      "id": 67,
      "title": "string2",
      "body": "string",
      "userName": "string2",
      "createdAt": "2023-01-02T09:06:11.887259",
      "lastModifiedAt": "2023-01-02T09:06:11.887259"
    }, {
      "id": 51,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T08:11:08.499926",
      "lastModifiedAt": "2022-12-27T08:11:08.499926"
    }, {
      "id": 50,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T08:09:12.490571",
      "lastModifiedAt": "2022-12-27T08:09:12.490571"
    }, {
      "id": 49,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:58:21.248993",
      "lastModifiedAt": "2022-12-27T07:58:21.248993"
    }, {
      "id": 48,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:46:17.102629",
      "lastModifiedAt": "2022-12-27T07:46:17.102629"
    }, {
      "id": 46,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:22:20.020946",
      "lastModifiedAt": "2022-12-27T07:22:20.020946"
    }, {
      "id": 45,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:19:58.592971",
      "lastModifiedAt": "2022-12-27T07:19:58.592971"
    }, {
      "id": 44,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:18:13.441116",
      "lastModifiedAt": "2022-12-27T07:18:13.441116"
    }, {
      "id": 43,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:17:20.79841",
      "lastModifiedAt": "2022-12-27T07:17:20.79841"
    }, {
      "id": 42,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:16:04.743408",
      "lastModifiedAt": "2022-12-27T07:16:04.743408"
    }, {
      "id": 41,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:13:54.795969",
      "lastModifiedAt": "2022-12-27T07:13:54.795969"
    }, {
      "id": 40,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:12:09.968914",
      "lastModifiedAt": "2022-12-27T07:12:09.968914"
    }, {
      "id": 39,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:10:24.596607",
      "lastModifiedAt": "2022-12-27T07:10:24.596607"
    }, {
      "id": 38,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:09:06.23095",
      "lastModifiedAt": "2022-12-27T07:09:06.23095"
    }, {
      "id": 37,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:08:06.889224",
      "lastModifiedAt": "2022-12-27T07:08:06.889224"
    }, {
      "id": 36,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:06:41.100342",
      "lastModifiedAt": "2022-12-27T07:06:41.100342"
    }, {
      "id": 35,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:03:24.656661",
      "lastModifiedAt": "2022-12-27T07:03:24.656661"
    }, {
      "id": 34,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T07:01:31.318696",
      "lastModifiedAt": "2022-12-27T07:01:31.318696"
    }, {
      "id": 33,
      "title": "hello-title",
      "body": "hello-body",
      "userName": "kyeongrok22",
      "createdAt": "2022-12-27T06:50:32.575795",
      "lastModifiedAt": "2022-12-27T06:50:32.575795"
    }],
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

## 댓글 기능

### 1. ``POST`` api/v1/posts/{postId}/comments
- 댓글을 작성한다.

> 댓글 작성 성공 시

```json
{
	"resultCode": "SUCCESS",
	"result":{
		"id": 4,
		"comment": "comment test4",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:15:04.270741"
	}
}
```

> 댓글 작성 실패 시

1. 로그인 하지 않은 경우
2. 게시물이 존재하지 않는 경우

### 2. ``GET`` api/v1/posts/{postId}/comments
- 댓글을 조회한다.

> 댓글 조회 성공 시

```json
{
	"resultCode": "SUCCESS",
	"result":{
	"content":[
		{
		"id": 3,
		"comment": "comment test3",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:07:25.699346"
		},
		{
		"id": 2,
		"comment": "comment test2",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:03:30.670768"
		}
	],
	"pageable":{"sort":{"empty": false, "sorted": true, "unsorted": false }, 
	"offset": 0,…},
	"last": true,
	"totalPages": 1,
	"totalElements": 2,
	"size": 10,
	"number": 0,
	"sort":{
	"empty": false,
	"sorted": true,
	"unsorted": false
	},
	"numberOfElements": 2,
	"first": true,
	"empty": false
	}
}
```

### 3. ``PUT`` api/v1/posts/{postId}/comments/{id}
- 댓글을 수정한다.

> 댓글 수정 성공 시

```json
{
	"resultCode": "SUCCESS",
	"result":{
		"id": 4,
		"comment": "modify comment",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:15:04.270741"
		}
}
```

> 댓글 수정 실패 시

1. 인증 실패한 경우
2. 댓글이 존재하지 않는 경우
3. 작성자 불일치한 경우
4. 데이터베이스 에러가 난 경우

### 4. ``DELETE`` api/v1/posts/{postId}/comments/{id}
- 댓글을 삭제한다.

> 댓글 삭제 성공 시

```json
{
	"resultCode": "SUCCESS",
	"result":{
		"message": "댓글 삭제 완료",
		"id": 4
		}
}
```

> 댓글 삭제 실패 시: 댓글 수정 실패와 동일함.

## 마이 피드 기능


### 1. ``GET`` api/v1/posts/my
- 내가 (로그인된 사용자가) 작성한 포스트를 모두 조회한다.

> 마이피드 조회 성공 시:

```json
{
  "resultCode": "SUCCESS",
  "result":{
    "content":[
			{
			"id": 4,
			"title": "test",
			"body": "body",
			"userName": "test",
			"createdAt": "2022-12-16T16:50:37.515952"
			}
		],
	"pageable":{
			"sort":{"empty": true, "sorted": false, "unsorted": true }, "offset": 0,…},
			"last": true,
			"totalPages": 1,
			"totalElements": 1,
			"size": 20,
			"number": 0,
			"sort":{
			"empty": true,
			"sorted": false,
			"unsorted": true
			},
			"numberOfElements": 1,
	"first": true,
	"empty": false
}
```

> 마이피드 조회 실패 시:

1. 로그인 하지 않은 경우


## 좋아요 기능

### 1. ``POST`` /api/v1/posts/{postId}/likes
- 포스트에 좋아요를 등록한다.

좋아요를 나타내는 Like 엔티티는 다음과 같다:

![img_2.png](img_2.png)

> 좋아요 등록 성공 시

```json
{
	"resultCode":"SUCCESS",
	"result": "좋아요를 눌렀습니다."
}
```

> 좋아요 등록 실패 시

1. 로그인 하지 않은 경우
2. 호출 시 명시한 고유 아이디를 가진 포스트가 없는 경우


## 알람 기능

### 1. ``GET`` /api/v1/alarms
- 현재 로그인 된 사용자가 받은 알람을 모두 조회한다.

알람을 나타내는 Alarm 엔티티는 다음과 같다:

![img_3.png](img_3.png)

> 알람 목록 조회 성공 시

```json
{
  "resultCode":"SUCCESS",
  "result": {
  "content":
  [
    {
       "id": 1,
       "alarmType": "NEW_LIKE_ON_POST",
       "fromUserId": 1,
       "targetId": 1,
       "text": "new like!",
       "createdAt": "2022-12-25T14:53:28.209+00:00",
    }
  ]
  }
}
```

> 알람 목록 조회 실패 시

1. 로그인 하지 않은 경우


## 에러가 발생하는 경우의 JSON 응답 형태

1. 로그인하지 않은 채 인증이 필요한 API (마이피드, 포스트/댓글 작성, 알람 목록 조회) 를 호출했을 때 OR 자신이 작성하지 않은 포스트/댓글에 대해 수정/삭제를 하고자 할 때
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```

2. 존재하지 않는 포스트/댓글을 조회했을 때
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "COMMENT_NOT_FOUND",
    "message": "해당 댓글이 없습니다."
  }
}
```

3. 포스트/댓글을 작성했던 사용자가 더 이상 회원으로 남아 있지 않을 때
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "USERNAME_NOT_FOUND",
    "message": "해당 사용자 아이디가 없습니다."
  }
}
```

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
1. 이슈를 생성할 때 각 이슈별로 명시된 것 이상의 기능을 구현하지 않도록 유의    2. 최대한 작은 단위로 기능을 묶어서 이슈로 등록 후 구현하도록 함  
```  

### 2. CI/CD 적용

- **수행 과정**
``` 1. 프로젝트 배포의 자동화를 위해 GitLab의 CI 구축 기능인 파이프라인 에디터로 YAML 파일을 작성  
2. YAML 파일 작성을 위해 tutorialspoint 와 stackoverflow 를 참고  
3. Docker Build 후 이미지를 생성하였으므로 docker docs 공식문서를 참고하여 Dockerfile 을 작성  
```  
- **참고 문헌**

  - [YAML - Indentation and Separation](https://www.tutorialspoint.com/yaml/yaml_indentation_and_separation.htm)  
  - [YAML How many spaces per indent?](https://stackoverflow.com/questions/42247535/yaml-how-many-spaces-per-indent)

# 📢 2주차 미션 요약

- 아래는 1주차에 이어서 프로젝트를 진행하면서 실질적으로 변경한 부분들과 읽고 도움 받은 글들의 출처이다.

### 1. ``Auditing`` 적용 시 날짜/시간 데이터 형식 변경으로 초단위만큼만 DB 저장 및 JSON 출력되도록 변경:

> [Formatting a Java 8 LocalDateTime in JSON with Spring Boot](https://jworks.io/formatting-a-java-8-localdatetime-in-json-with-spring-boot/)
>
> [Formatting json Date/LocalDateTime/LocalDate in Spring Boot](https://www.springcloud.io/post/2022-09/springboot-date-format/#gsc.tab=0)
>
> [[JPA] BaseTimeEntity LocalDateTime format 변경](https://mchch.tistory.com/165)
>
> [How can I parse/format dates with LocalDateTime? (Java 8)](https://stackoverflow.com/questions/22463062/how-can-i-parse-format-dates-with-localdatetime-java-8)

- DB 에서 눈으로 테이블의 레코드들을 확인해야 할 때가 있었는데, 이전에 비해 가독성이 향상되어 DB 를 확인할 때의 피로를 감소시킬 수 있었다.


### 2. 테스트 코드 리팩토링

- @ParameterizedTest 를 활용하여 여러 테스트 메서드에서 중복되는 부분을 매개 변수로 분리하고,<br>
  @MethodSource 에 정의한 메서드를 활용해 테스트 상황마다 필요한 매개 변수를 주입할 수 있도록 함

> [Guide to JUnit 5 Parameterized Tests](https://www.baeldung.com/parameterized-tests-junit-5)

``PostResponse``, ``CommentDeleteResponse`` 응답 DTO 에 인터페이스와 상속을 도입하여<br>
매개 변수가 없는 성공 테스트도 공통된 부분을 메서드로 분리할 수 있도록 변경.

> [Composition vs. Inheritance: How to Choose?](https://www.thoughtworks.com/insights/blog/composition-vs-inheritance-how-choose)

- 반복되는 테스트 (성공 테스트 포함) 를 커스터마이징을 해서 좀 더 반복을 줄이고 빠르게 테스트를 작성할 수 있게 되었다.<br>
- ``PostServiceTest`` 의 포스트 수정과 삭제 실패 테스트의 개수 변경 전 6개 -> 변경 후 3개
- ``CommentServiceTest`` 의 댓글 수정과 삭제 실패 테스트의 개수 변경 전 6개 -> 변경 후 3개

> [테스트 코드 리팩토링 하기](https://itistori.tistory.com/51)

### 3. 포스트/댓글/좋아요에 Soft Delete 적용
- Soft Delete 는 실제로 데이터가 삭제되지 않고 삭제 되었는지 여부를 나타내는 컬럼을 업데이트하는 방식으로 삭제를 꾸며내는 것을 말한다.

H2를 활용해 Soft Delete 가 실행되는지 확인하는 DB 테스트를 진행하기에 앞서 아래 글을 읽고 H2 DB 가 무엇인지 공부한 후 진행했다:

> [h2란?](https://www.javatpoint.com/spring-boot-h2-database)

- H2 를 사용해 DB 테스트를 진행하면서, ``referential Integrity Constraints failed`` 와 ``could not prepare statement``<br>
  두 개의 오류를 마주쳤다.

> 참조 무결성 문제 해결하기: 블로그 포스트 작성 예정

> [Prepare Statement 할 수 없는 이유](https://stackoverflow.com/questions/24060498/org-hibernate-exception-sqlgrammarexception-could-not-prepare-statement)

> [H2 에서 사용되는 예약어]( http://www.h2database.com/html/advanced.html)

> [H2 활용해 Soft Delete 테스트하기](https://itistori.tistory.com/53)

- H2를 활용한 DB 테스트를 진행해서 실제 기능을 구현하기 전에 어떤 동작을 목표로 기능이 구현되어야 하는지를 테스트 코드를 작성하여<br>
  개요를 짤 수 있었다.


### 4. 특정 API 주소는 인증 없이도 허용되게, 특정 API 는 인증 필요하게 만들기
- 특정 포스트 상세 조회, 특정 포스트의 댓글 조회, 특정 포스트의 좋아요 조회 시 공통적으로 postId 가 API 경로에 포함되어 있었다.<br>
  단순히 {postId} 로 작성한 후 의도한 대로 인증 요청이 되지 않아서 구글링을 한 결과, API URL 의 ``PathVariable`` 부분은<br>
  변수명만이 아니라 변수의 데이터형을 함께 ``regex`` (정규식) 으로 명시해주어야 한다는 것을 알 수 있었다.

> [정수형 @PathVariable 변수를 API 경로 내에서 정규식으로 표현하기](https://stackoverflow.com/questions/55863235/how-to-apply-spring-security-antmatchers-pattern-only-to-url-with-pathvariable)

> [경로에 @PathVariable 포함하는 API 호출 시 인증 필요 여부 설정하기](https://itistori.tistory.com/50)

## 📝회고

### 1. 테스트 코드
- ``Fixture`` 클래스를 사용해서 서비스 테스트 내에서 매번 ``setUp()`` 에서 객체를 생성하는 일을 줄여야 했는데, 결국 시간 관계상 지저분하게 내버려두었다.
- ``PostControllerTest`` 에서 모든 등록된 포스트를 조회할 때 최신순으로 포스트 정보들이 나열되는지를 테스트하지 못했다.

### 2. 컨트롤러
- 등록된 모든 포스트를 조회하여 나열할 때, 최신 순이 아니라 포스트의 고유 아이디가 높은 순대로 정렬하도록 ``@PageDefault`` 에서 설정했다.

### 3. 예외 처리
- 포스트를 수정하거나 삭제할 때에 데이터베이스 상에서 오류가 날 수 있는 상황에 대해 생각해보았으나, 결국 생각해내지 못했다.<br>  
  요구 사항에 충실해서 처리해야 하는 예외 상황들을 모두 잡고 싶었지만 요구 사항에 대한 이해의 부족에서 온 실패라고 생각한다.

### 4. Merge Request
- 각 기능별로 이슈를 만들었고, 프로젝트 기간동안 이슈별로 브랜치를 만들고 작업이 완료된 후 미션 1의 브랜치인 ``sprint1``에 병합하는 방식을 사용했다.<br>  
  중간에 하나의 기능에 대한 이슈 브랜치를 ``sprint1`` 이 아닌 ``main`` 브랜치에 병합을 하는 **대형사고(!)** 가 발생해서 ``revert`` 를 사용했고,<br>  
  ``main`` 브랜치의 디렉토리에 ``revert`` 기록이 남게 되었다. 마지막에 ``sprint1`` 을 ``main`` 으로 병합할 때 ``revert`` 기록과 ``sprint1`` 에서 진행된 수정사항들이 충돌해서<br>  
  ``merge conflict`` 가 발생했다.

### 5. README 를 사용한 문서화
- 코드 작성과 문서 작성을 동시에 하자.

### 6. 계획적인 코드 작성
- 매일 주어진 미션을 완료하는 것을 목표로 코드를 작성해야 정해진 기한 안에 모든 기능 요구사항들을 충족시킬 수 있다.

## 궁금했던 점

- ``Comment`` 와 ``Post`` 의 관계를 identifying 로 할지 non-identifying 으로 할 지 고민이 된다. 각 포스트별로 달린 댓글의 순번을 정하는 게 더 상식적이다.
- 왜 API 경로 내의 정규식은 백슬래쉬가 두 개일까? 정수 표현: ``\\d+``
  - 원래 ``Java`` ``regex`` 에서는 정수 표현: ``\d`` 로 백슬래쉬가 하나이다.
- ``LikeEntity`` 엔티티를 서비스 내에서 빌더 패턴을 써서 만들어 사용하고 있는데, 서비스에서 직접 생성하는 것이 맞을까,<br>
  ``DDD(Domain Driven Development)`` 로 엔티티 내부에서 스스로를 생성하는 것이 적절할까?
- H2 테스트 시 테스트의 범위를 어디까지 생각하고 테스트 코드를 작성해야 하는지?
  - user 를 포함해야 하는지, post 와 like 만 포함하면 되는지
  - comment 를 테스트 해야 하는지

## 구현하고 싶은 점
- User 탈퇴
- AOP 적용
  - 관점 지향 프로그래밍 적용하여 코드 추상화 수준 및 재사용성 향상